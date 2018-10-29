package com.example.robmillaci.nytnews.Utils;

/*
this class stores the static final URLS used by the application to download data.
The API key is private to this class
 */
public class Constants {
    private static final String API_KEY = "166a1190cb80486a87ead710d48139ae";
    public static final String POPULAR_NEWS_URL = "http://api.nytimes.com/svc/topstories/v2/home.json?api-key=" + API_KEY;
    public static final String MOST_POPULAR_FOOD_URL= "https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Food/30.json?api-key=" + API_KEY;
    public static final String MOST_POPULAR_BUSINESS_URL = "https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Business%20Day/30.json?api-key=" + API_KEY;
    public static final String ARTICLE_SEARCH_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=" + API_KEY;
    public static final String MOST_POPULAR_MOVIES_URL = "https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Movies/30.json?api-key=" + API_KEY;
    public static final String MOST_POPULAR_SCIENCE_URL = "https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Science/30.json?api-key="+API_KEY;
    public static final String ARTICLE_SEARCH_PARAMS = "&hl=true&q=";

}
