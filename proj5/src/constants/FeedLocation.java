/*
 * the various feed name + location used
 */
package constants;

import java.util.HashMap;

/**
 * @author Jason Zerbe
 */
public interface FeedLocation {

    HashMap<String, String> kFeedMap = new HashMap<String, String>() {

        private static final long serialVersionUID = 42L;

        {
            put("CNN", "http://rss.cnn.com/rss/cnn_world.rss");
            put("ABC", "http://feeds.abcnews.com/abcnews/internationalheadlines");
            put("NBC", "http://rss.msnbc.msn.com/id/3032091/device/rss/rss.xml");
            put("BBC", "http://newsrss.bbc.co.uk/rss/newsonline_uk_edition/world/rss.xml");
        }
    };
}
