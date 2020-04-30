package com.example.android.gson;

import java.io.Serializable;

public class TradeGoods implements Serializable {
    private int id;
    private String nick_NAME;
    private String sex;
    private String photo;
    private String introduction;
    private String user_ID;
    private String photos;
    private String title;
    private String category;
    private String price;

    public String getNick_NAME() {
        return nick_NAME;
    }

    public int getId() {
        return id;
    }

    public String getSex() {
        return sex;
    }

    public String getPhoto() {
        return photo;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public String getTitle() {
        return title;
    }

    public String getPhotos() {
        return photos;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }
}
