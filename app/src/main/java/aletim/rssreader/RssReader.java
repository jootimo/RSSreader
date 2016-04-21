package aletim.rssreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Pääluokka RSS-syötteen esittämiselle listanäkymässä
 */
public class RssReader extends AppCompatActivity {
    public static final String DEFAULT_URL = "http://www.muropaketti.com/feed/";

    /**
     * Rakentaa käyttöliittymän
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Syötteen haku:
        update();
        //Vedä ylös päivittääksesi:
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Luo piilotetun valikon
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    /**
     * Tapahtumakäsittelijä valikon alkioille
     * @param item klikattu alkio
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    /**
     * Päivittää käyttöliittymän, kun palataan SettingsActivitystä
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Kun palataan SettingsActivitystä, ladataan RSS-syöte uudelleen
        update();
        recreate();
    }


    /**
     * Hakee RSS-syötteen ruudulle tallennettujen asetusten perusteella (URL)
     */
    public void update(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //kutsutaan GetRssTask:in doInBackground-metodia, toinen parametri on varaosoite
        new GetRssTask(this).execute(prefs.getString("url", DEFAULT_URL));
    }

    /**
     * Asetetaan jäsennetty syöte listanäkymään ja annetaan jokaiselle otsikolle tapahtumankäsittelijä
     * @param feed RSS-syöte
     */
    public void setList(final RssFeed feed){
        ArrayList<String> titles = feed.getTitles();
        ListView listview = (ListView) findViewById(R.id.list);

        //Luodaan uusi adapteri, joka huolehtii ArrayListin esittämisestä ListViewissä
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        listview.setAdapter(adapter);

        // Tapahtumankäsittelijä listan uutisille
        AdapterView.OnItemClickListener itemClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                System.out.println("Link: " + feed.getItems().get(position).getLink());
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(feed.getItems().get(position).getLink()));
                startActivity(browser);

            }
        };
        listview.setOnItemClickListener(itemClickedHandler);

        //Kanavan nimi yläpalkkiin:
        TextView textView = (TextView) findViewById(R.id.titleText);
        textView.setText(feed.getChannelName());
    }



}
