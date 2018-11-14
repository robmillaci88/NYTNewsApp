package com.example.robmillaci.nytnews.Models;

/*
this class is used for constructing news objects from the downloaded JSON data
 */


public class NewsObjectModel {
    private final String section;
    private final String subsection;
    private final String title;
    private final String abStract;
    private final String link;
    private final String byLine;
    private final String pubDate;
    private final String imgUrl;

    public NewsObjectModel(String section, String subsection, String title, String abStract, String url, String byLine, String pubDate, String imgUrl) {
        this.section = section;
        this.subsection = subsection;
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

    public String getSubsection() {
        return subsection;
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
