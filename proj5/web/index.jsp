<%--
    Document   : index
    Created on : Apr 13, 2012, 3:44:33 PM
    Author     : Jason Zerbe
--%>

<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE html>
<html>
    <head>
        <title>proj5 - RSS Feed Reader</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.css" />
        <script src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
        <script src="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.js"></script>
        <link rel="shortcut icon" href="favicon.ico" />
    </head>
    <body>
        <div data-role="page">

            <div data-role="header">
                <h1>proj5 - RSS Feed Reader</h1>
            </div><!-- /header -->

            <div data-role="content">
                <div data-role="fieldcontain">
                    <label for="news-source" class="select">Choose news source:</label>
                    <select name="search-sources" id="news-source" multiple="multiple">
                        <%
                            HashMap<String, String> aFeedMap = constants.FeedLocation.kFeedMap;
                            for (String aKey : aFeedMap.keySet()) {
                        %>
                        <option value="<%= aKey%>"><%= aKey%></option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <div data-role="fieldcontain">
                    <label for="search-keywords">Search Keywords:</label>
                    <input type="search" name="search-terms" id="search-keywords" placeholder="space seperated search terms" />
                </div>
                <div data-role="fieldcontain">
                    <label>Match Keywords:</label>
                    <fieldset id="horiz-search-type" data-role="controlgroup" data-type="horizontal" >
                        <label for="radio-choice-any">ANY</label>
                        <input type="radio" name="search-type" id="radio-choice-any" value="any"  />
                        <label for="radio-choice-all">ALL</label>
                        <input type="radio" name="search-type" id="radio-choice-all" value="all"  />
                    </fieldset>
                </div>

                <div data-role="fieldcontain">
                    <fieldset data-role="controlgroup">
                        <legend>Elements to search:</legend>
                        <input type="checkbox" name="ele-title" id="ele-title" class="custom" />
                        <label for="ele-title">Title</label>
                        <input type="checkbox" name="ele-description" id="ele-description" class="custom" />
                        <label for="ele-description">Description</label>
                    </fieldset>
                </div>

                <div data-role="fieldcontain">
                    <button type="submit">Search</button>
                </div>
            </div><!-- /content -->

        </div><!-- /page -->
    </body>
</html>
