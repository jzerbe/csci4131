/*
 * GET - filtered RSS feed information output as XHTML
 */
package servlets;

import constants.FeedLocation;
import constants.PageData;
import constants.SearchParams;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

/**
 * @author Jason Zerbe
 */
public class Feeds extends HttpServlet {

    private static final long serialVersionUID = 42L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] aSearchSourceArray = request.getParameterValues(SearchParams.kParamSearchSourceStr);
        if (aSearchSourceArray == null) {
            response.sendError(400, "Malformed Request: missing any search sources");
        } else {
            ArrayList<String> aSourceUrlStrList = new ArrayList<String>();

            for (String aSearchSource : aSearchSourceArray) { // need to find and add all source URL Strings
                if (!"".equals(aSearchSource)) {
                    for (String aFeedKey : FeedLocation.kFeedMap.keySet()) {
                        if (aSearchSource.equals(aFeedKey)) {
                            aSourceUrlStrList.add(FeedLocation.kFeedMap.get(aFeedKey));
                        }
                    }
                }
            }

            if (aSourceUrlStrList.isEmpty()) {
                response.sendError(400, "Malformed Request: no valid search sources passed");
            } else {
                // process raw keyword data
                String aSearchKeywordRawStr = request.getParameter(SearchParams.kParamSearchTermStr);
                if ((aSearchKeywordRawStr == null) || ("".equals(aSearchKeywordRawStr))) {
                    response.sendError(400, "Malformed Request: missing keywords");
                } else {
                    String[] aSearchKeywordArray = aSearchKeywordRawStr.split(" ");

                    // how shall we match keywords?
                    String aSearchTypeStr = request.getParameter(SearchParams.kParamSearchTypeStr);
                    boolean aMatchAllKeyWords = false;
                    if ((aSearchTypeStr != null) && (aSearchTypeStr.equals("all"))) {
                        aMatchAllKeyWords = true;
                    }

                    // what fields shall we search?
                    boolean aSearchElementTitle = false;
                    String aSearchElementTitleStr = request.getParameter(SearchParams.kParamSearchElementTitleStr);
                    if ((aSearchElementTitleStr != null) && ("on".equals(aSearchElementTitleStr))) {
                        aSearchElementTitle = true;
                    }
                    boolean aSearchElementDesc = false;
                    String aSearchElementDescStr = request.getParameter(SearchParams.kParamSearchElementDescStr);
                    if ((aSearchElementDescStr != null) && ("on".equals(aSearchElementDescStr))) {
                        aSearchElementDesc = true;
                    }
                    if (!aSearchElementTitle && !aSearchElementDesc) {
                        response.sendError(400, "Malformed Request: no elements selected to search");
                    } else {
                        // get RSS data and parse it for output
                        ArrayList<models.RssItem> aRssItemList = new ArrayList<models.RssItem>();
                        RssParser aRssParser = new RssParser();
                        for (String aSourceUrlStr : aSourceUrlStrList) {
                            RssFeed aRssFeed;
                            try {
                                aRssFeed = aRssParser.load(aSourceUrlStr);
                            } catch (Exception ex) {
                                Logger.getLogger(Feeds.class.getName()).log(Level.WARNING, null, ex);
                                continue;
                            }

                            for (RssItemBean aRssItemBean : aRssFeed.getItems()) {
                                String aItemTitle = aRssItemBean.getTitle();
                                String aItemDesc = aRssItemBean.getDescription();
                                String aItemLink = aRssItemBean.getLink();

                                boolean aItemAddFlag = false;
                                if (aSearchElementTitle && stringMatchesKeywords(aItemTitle, aSearchKeywordArray, aMatchAllKeyWords)) {
                                    aItemAddFlag = true;
                                }
                                if (aSearchElementDesc && stringMatchesKeywords(aItemDesc, aSearchKeywordArray, aMatchAllKeyWords)) {
                                    aItemAddFlag = true;
                                }

                                if (aItemAddFlag) {
                                    aRssItemList.add(new models.RssItem(aItemTitle, aItemDesc, aItemLink));
                                }
                            }
                        }

                        // output found RssItems as HTML
                        response.getWriter().println("<div data-role='page'>"
                                + "<div data-role='header'>"
                                + "<a href='javascript:goBack();' data-role='button' data-icon='back'>Back</a>"
                                + "<h1>" + PageData.kResultsPageTitleStr + "</h1>"
                                + "</div><div data-role='content'>");
                        for (models.RssItem aRssItem : aRssItemList) {
                            response.getWriter().println("<div data-role='collapsible'>"
                                    + "<h3>" + aRssItem.getTitle() + "</h3>"
                                    + "<p>"
                                    + aRssItem.getDesc()
                                    + " [<a rel='external' href='" + aRssItem.getLink() + "'>LINK</a>]"
                                    + "</p>"
                                    + "</div>");
                        }
                        response.getWriter().println("</div></div>"); // close up content and page
                    }
                }
            }
        }
    }

    /**
     * case-insensitive matching of a string with an array of keywords; use
     * matchAllKeywords boolean flag to match all keywords in theKeywords array
     * @param theString String
     * @param theKeywords String[]
     * @param matchAllKeywords boolean
     * @return boolean - did theString match theKeywords?
     */
    public static boolean stringMatchesKeywords(String theString, String[] theKeywords, boolean matchAllKeywords) {
        if ((theString == null) || (theKeywords == null)) {
            return false;
        } else {
            theString = theString.toLowerCase(Locale.ENGLISH);

            boolean matchOneKeywordBoolean = false;
            for (String aKeyword : theKeywords) {
                if ("".equals(aKeyword) || " ".equals(aKeyword)) {
                    continue;
                }

                aKeyword = aKeyword.toLowerCase(Locale.ENGLISH);
                if ((matchAllKeywords) && (!theString.contains(aKeyword))) {
                    return false;
                } else if (theString.contains(aKeyword)) {
                    matchOneKeywordBoolean = true;
                }
            }

            if ((!matchAllKeywords) && (!matchOneKeywordBoolean)) {
                return false;
            }

            return true;
        }
    }
}
