package com.example.android.gson;

import java.io.Serializable;

public class TradeGoods implements Serializable {
    //    这里因为和gson字段名字一致，所以没用@SerialName()。
    private int id;
    private String nick_NAME;
    private String sex;
    private String photo;
    private String introduction;
    private String user_ID;
    private String photos;
    private String title;

    public TradeGoods() {
    }

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

    public void setId(int id) {
        this.id = id;
    }

    public void setNick_NAME(String nick_NAME) {
        this.nick_NAME = nick_NAME;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "TradeGoods{" +
                "id=" + id +
                ", nick_NAME='" + nick_NAME + '\'' +
                ", sex='" + sex + '\'' +
                ", photo='" + photo + '\'' +
                ", introduction='" + introduction + '\'' +
                ", user_ID='" + user_ID + '\'' +
                ", photos='" + photos + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }
}
