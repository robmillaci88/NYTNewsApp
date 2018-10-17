package com.example.robmillaci.nytnews.Models;

public class TopNewsObjectModel {
    String section;
    String title;
    String abStract;
    String link;
    String byLine;
    String pubDate;
    String imgUrl;

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