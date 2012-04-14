/*
 * class for structuring and storing an RssItem's data
 */
package models;

/**
 * @author Jason Zerbe
 */
public class RssItem {

    protected String myItemTitle;
    protected String myItemDesc;
    protected String myItemLink;

    public RssItem(String theItemTitle, String theItemDesc, String theItemLink) {
        myItemTitle = theItemTitle;
        myItemDesc = theItemDesc;
        myItemLink = theItemLink;
    }

    public String getTitle() {
        return myItemTitle;
    }

    public String getDesc() {
        return myItemDesc;
    }

    public String getLink() {
        return myItemLink;
    }
}
