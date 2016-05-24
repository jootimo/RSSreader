package aletim.rssreader;

import android.content.Context;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * RSS-syotteen asynkroninen hakutehtava
 */
public class GetRssTask extends AsyncTask<String, Void, RssFeed> {
    URL url;
    InputStream in;
    RssFeed feed;
    Context context;

    /**
     * Konstruktori, joka saa parametrinaan viittauksen kutsujaansa
     * @param context viittaus kutsujaan
     */
    public GetRssTask(Context context) {
        this.context = context;
    }

    /**
     * Hakee RSS-syotteen ja jasentaa sen
     * @param urls osoite, josta syote haetaan
     * @return RSS-syote
     */
    @Override
    protected RssFeed doInBackground(String... urls) {
        try {
            this.url = new URL(urls[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            System.out.println("Connection made to " + url.toString());
            in = conn.getInputStream();
            feed = new RssFeed(in);
            feed.parse();
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

    /**
     * Asettaa jasennetyn RSS-syotteen paanakyman listaan
     * @param feed RSS-syote
     */
    @Override
    protected void onPostExecute(RssFeed feed) {
        try {
            RssReader reader = (RssReader) context;
            reader.setList(feed);
        } catch (NullPointerException e) {
            System.out.println("Syötettä ei löytynyt");
            e.printStackTrace();
        }
    }
}