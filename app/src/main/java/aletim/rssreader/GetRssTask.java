package aletim.rssreader;

import android.content.Context;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Sisäluokka uudessa säikeessä tehtävälle RSS-syötteen haulle
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
     * Hakee RSS-syötteen ja jäsentää sen
     * @param urls osoite, josta syöte haetaan
     * @return RSS-syöte
     */
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
            //TODO: Syötteestä pitäisi hakea encoding ja luoda responseString sen mukaan
            String responseString = new String(response, "UTF-8");
            feed = new RssFeed(responseString);
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
     * Asettaa jäsennetyn RSS-syötteen päänäkymän listaan
     * @param feed RSS-syöte
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