package aletim.rssreader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        //kutsutaan GetRssTask:in doInBackground-metodia
        new GetRssTask().execute("http://muropaketti.com/feed/");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                String responseString = new String(response, "UTF-8");
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
            setText(feed.getFeed());
        }
    }
}
