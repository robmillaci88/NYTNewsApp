package com.example.robmillaci.nytnews.Models;

public class NewsObjectModel {
    private String section;
    private String subsection;
    private String title;
    private String abStract;
    private String link;
    private String byLine;
    private String pubDate;
    private String imgUrl;

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
