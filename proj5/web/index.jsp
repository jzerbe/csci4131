<%--
    Document   : index
    Created on : Apr 13, 2012, 3:44:33 PM
    Author     : Jason Zerbe
--%>

<%@page import="constants.PageData"%>
<%@page import="constants.SearchParams"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE html>
<html>
    <head>
        <title><%= PageData.kFrontPageTitleStr%></title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.css" />
        <script src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
        <script src="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.js"></script>
        <link rel="shortcut icon" href="favicon.ico" />
        <script type="text/javascript">
            // jQM back
            function goBack() {
                history.back();
                return false;
            }
        </script>
    </head>
    <body>
        <div data-role="page">

            <div data-role="header">
                <h1><%= PageData.kFrontPageTitleStr%></h1>
            </div><!-- /header -->

            <div data-role="content">

                <form action="<%= SearchParams.kParamSearchSubmitLocStr%>" method="get">

                    <div data-role="fieldcontain">
                        <label for="news-source" class="select">Choose news source:</label>
                        <select name="<%= SearchParams.kParamSearchSourceStr%>" id="news-source" multiple="multiple" data-native-menu="false">
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
                        <input type="search" name="<%= SearchParams.kParamSearchTermStr%>" id="search-keywords" placeholder="space seperated search terms" />
                    </div>

                    <div data-role="fieldcontain">
                        <fieldset data-role="controlgroup">
                            <legend>Match Keywords:</legend>
                            <label for="radio-choice-all">ALL</label>
                            <input type="radio" name="<%= SearchParams.kParamSearchTypeStr%>" id="radio-choice-all" value="<%= SearchParams.kParamSearchTypeAllStr%>" />
                            <label for="radio-choice-any">ANY</label>
                            <input type="radio" name="<%= SearchParams.kParamSearchTypeStr%>" id="radio-choice-any" value="<%= SearchParams.kParamSearchTypeAnyStr%>" />
                        </fieldset>
                    </div>

                    <div data-role="fieldcontain">
                        <fieldset data-role="controlgroup">
                            <legend>Elements to search:</legend>
                            <input type="checkbox" name="<%= SearchParams.kParamSearchElementTitleStr%>" id="ele-title" class="custom" />
                            <label for="ele-title">Title</label>
                            <input type="checkbox" name="<%= SearchParams.kParamSearchElementDescStr%>" id="ele-description" class="custom" />
                            <label for="ele-description">Description</label>
                        </fieldset>
                    </div>

                    <button type="submit" name="submit" value="submit-value">Search</button>

                </form>

            </div><!-- /content -->

        </div><!-- /page -->
    </body>
</html>
