package com.example.robmillaci.nytnews.Models;

public class SearchNewsObjectModel {
   private String headline;
    private String snippet;
    public String pubDate;
    public String webLink;
    private String imageUrl;

    public SearchNewsObjectModel(String headline, String snippet, String pubDate, String webLink, String imageUrl) {
        this.headline = headline;
        this.snippet = snippet;
        this.pubDate = pubDate;
        this.webLink = webLink;
        this.imageUrl = imageUrl;
    }

    public String getWebLink() {
        return webLink;
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
