package com.BangumiList.bangumi;

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
