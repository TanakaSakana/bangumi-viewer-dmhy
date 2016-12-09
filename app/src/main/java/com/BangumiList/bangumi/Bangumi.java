package com.BangumiList.bangumi;

import android.util.Log;

import com.dmhyparser.info.BangumiInfo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Bangumi implements Serializable {
    String Name;
    String Image;
    String Link;
    String Description;
    List<String> Keyword;

    public Bangumi(String name, String image, String link, String keyword) {
        Name = name;
        Image = image;
        Link = link;
        Keyword = Arrays.asList(keyword.split("\\|"));
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

    public List<String> getKeyword() {
        return Keyword;
    }
}
