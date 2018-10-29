package com.example.robmillaci.nytnews.Models;

/*
this class is used for constructing top news objects from the downloaded JSON data
 */

public class TopNewsObjectModel {
    private String section;
    private String title;
    private String abStract;
    private String link;
    private String byLine;
    private String pubDate;
    private String imgUrl;

    public TopNewsObjectModel(String section, String title, String abStract, String url, String byLine, String pubDate, String imgUrl) {
        this.section = section;
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

    public String getSection() {
        return section;
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