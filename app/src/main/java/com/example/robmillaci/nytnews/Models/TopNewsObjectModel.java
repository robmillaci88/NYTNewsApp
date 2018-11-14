package com.example.robmillaci.nytnews.Models;

/*
this class is used for constructing top news objects from the downloaded JSON data
 */

public class TopNewsObjectModel {
    private final String title;
    private final String abStract;
    private final String link;
    private final String byLine;
    private final String pubDate;
    private final String imgUrl;

    public TopNewsObjectModel(String title, String abStract, String url, String byLine, String pubDate, String imgUrl) {
        this.title = title;
        this.abStract = abStract;
        this.link = url;
        this.byLine = byLine;
        this.pubDate = pubDate;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }


    public String getTitle() {
        return title;
    }

    public String getAbStract() {
        return abStract;
    }

    public String getLink() {
        return link;
    }

    public String getByLine() {
        return byLine;
    }

    public String getPubDate() {
        return pubDate;
    }
}