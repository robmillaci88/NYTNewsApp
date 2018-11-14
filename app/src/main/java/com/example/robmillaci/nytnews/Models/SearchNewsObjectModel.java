package com.example.robmillaci.nytnews.Models;

/*
this class is used for constructing searched news objects from the downloaded JSON data
 */

public class SearchNewsObjectModel {
    private final String headline;
    private final String snippet;
    public final String pubDate;
    public final String webLink;
    private final String imageUrl;

    public SearchNewsObjectModel(String headline, String snippet, String pubDate, String webLink, String imageUrl) {
        this.headline = headline;
        this.snippet = snippet;
        this.pubDate = pubDate;
        this.webLink = webLink;
        this.imageUrl = imageUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
