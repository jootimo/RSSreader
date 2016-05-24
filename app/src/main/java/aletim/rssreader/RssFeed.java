package aletim.rssreader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Luokka RSS-syotteen kuvaamiseen
 */
public class RssFeed {
    InputStream in;
    String channelName;
    ArrayList<RssItem> items;

    public RssFeed(InputStream in){
        this.in = in;
    }

    public ArrayList<RssItem> getItems() {
        return items;
    }
    public String getChannelName() {
        return channelName;
    }
    public ArrayList<String> getTitles() {
        ArrayList<String> titles = new ArrayList<String>();
        for(RssItem item: items) {
            titles.add(item.getTitle());
        }
        return titles;
    }

    /**
     * Jäsentää syötteen listaksi RssItem-olioita
     */
    public void parse() {
        try {
            items = new ArrayList<RssItem>();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(in, null); //metodin 2. parametri olisi encoding, nyt se tunnustellaan automaattisesti
            int eventType = parser.getEventType();
            String text = "";
            RssItem item = new RssItem();
            boolean isChannelNameSet = false;
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();

                switch (eventType) {
                    case(XmlPullParser.START_TAG):
                        if(tag.equalsIgnoreCase("item")) {
                            item = new RssItem();
                        }
                        break;

                    case(XmlPullParser.TEXT):
                        text = parser.getText();
                        break;

                    case(XmlPullParser.END_TAG):
                        if(tag.equalsIgnoreCase("item")) {
                            items.add(item);
                        }
                        else if(tag.equalsIgnoreCase("title")) {
                            if(!isChannelNameSet) {
                                channelName = text;
                                isChannelNameSet = true;
                            }
                            item.setTitle(text);
                        }
                        else if(tag.equalsIgnoreCase("link")) {
                            item.setLink(text);
                        }

                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException xppe) {
            System.out.println("Parsing failed!");
            xppe.printStackTrace();
        }
        catch (IOException ioe) {
            System.out.println("Unable to find next eventType from feed");
            ioe.printStackTrace();
            }
        }
}

