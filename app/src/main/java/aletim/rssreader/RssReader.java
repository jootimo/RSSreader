package aletim.rssreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Pääluokka joka luo käyttöliittymän ja hakee RSS-syötteen
 */
public class RssReader extends AppCompatActivity {
    public static final String PREFERENCE_FILE = "preferences.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Syötteen haku:
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Valittiinko "Settings"
        if (id == R.id.action_settings) {
            //Avataan uusi activity
            Intent intent = new Intent();
            intent.setClass(RssReader.this, SettingsActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Kun palataan SettingsActivitystä, ladataan RSS-syöte uudelleen
        update();
    }

    /**
     * Hakee RSS-syötteen ruudulle tallennettujen asetusten perusteella (URL)
     */
    public void update(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //kutsutaan GetRssTask:in doInBackground-metodia, toinen parametri on varaosoite
        new GetRssTask().execute(prefs.getString("url", "http://www.iltasanomat.fi/rss/tuoreimmat.xml"));

    }

    /**
     * Asettaa käyttöliittymän TextView-kenttään tekstiä
     * @param text
     */
    public void setText(String text){
        TextView tv = (TextView) findViewById(R.id.text);
        tv.setText(text);
    }


    /**
     * Sisäluokka uudessa säikeessä tehtävälle RSS-syötteen haulle
     */
    private class GetRssTask extends AsyncTask<String, Void, RssFeed> {
        URL url;
        InputStream in;
        RssFeed feed;

        @Override
        protected RssFeed doInBackground(String... urls) {
            try {
                this.url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                System.out.println("Connection made to " + url.toString());
                in = conn.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int count; (count = in.read(buffer)) != -1; ) {
                    out.write(buffer, 0, count);
                }
                byte[] response = out.toByteArray();
                String responseString = new String(response, "iso-8859-1");
                feed = new RssFeed(responseString);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return feed;
        }

        @Override
        protected void onPostExecute(RssFeed feed) {
            try {
                setText(feed.getFeed());
            } catch (NullPointerException e) {
                setText("Syötettä ei löytynyt");
                e.printStackTrace();
            }
        }
    }
}
