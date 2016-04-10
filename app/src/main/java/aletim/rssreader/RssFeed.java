package aletim.rssreader;

/**
 * Luokka RSS-syötteen kuvaamiseen
 */
public class RssFeed {
    String feed;

    public RssFeed(){}
    public RssFeed(String feed){
        this.feed = feed;
    }

    public String getFeed(){
        return feed;
    }
}
