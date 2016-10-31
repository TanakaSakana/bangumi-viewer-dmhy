package com.BangumiList.bangumi;

import com.dmhyparser.info.BangumiInfo;

import java.io.Serializable;

public class Bangumi implements Serializable {
    String Name;
    String Image;
    String Link;
    String Description;
    String Keyword;

    public Bangumi(String name, String image, String link, String keyword) {
        Name = name;
        Image = image;
        Link = link;
        Keyword= keyword;
    }

    public Bangumi(String name, String image, String link) {
        Name = name;
        Image = image;
        Link = link;

    }

    public Bangumi(BangumiInfo bangumiInfo) {
        Name = bangumiInfo.getName();
        Image = bangumiInfo.getImage();
        Link = bangumiInfo.getHomepage();
        Description = bangumiInfo.getDescription();
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDescription() {
        return Description;
    }

    public String getName() {
        return Name;
    }

    public String getImageLink() {
        return Image;
    }

    public String getLink() {
        return Link;
    }

    public String getKeyword() {
        return Keyword;
    }
}
