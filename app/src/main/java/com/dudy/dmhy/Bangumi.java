package com.dudy.dmhy;

public class Bangumi {
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

    public String getImage() {
        return Image;
    }

    public String getLink() {
        return Link;
    }

}
