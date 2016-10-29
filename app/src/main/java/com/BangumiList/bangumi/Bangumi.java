package com.BangumiList.bangumi;

import com.dmhyparser.info.BangumiInfo;

import java.io.Serializable;

public class Bangumi implements Serializable {
    String Name;
    String Image;
    String Link;

    public Bangumi(String name, String image, String link) {
        Name = name;
        Image = image;
        Link = link;
    }

    public Bangumi(BangumiInfo bangumiInfo) {
        Name = bangumiInfo.getName();
        Image = bangumiInfo.getImg();
        Link = bangumiInfo.getHp();
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

}
