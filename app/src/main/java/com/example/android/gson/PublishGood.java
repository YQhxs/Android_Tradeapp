package com.example.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Description:
 * @Author: YQHxs
 * @Date: Created in 2020/5/18 21:36
 * @Modified By:
 */
public class PublishGood {
    @SerializedName("user_ID")
    private String useid;
    private String introduction;
    private String title;
    private String photos;
    private String category;
    private String price;

    public PublishGood() {
    }

    public String getUseid() {
        return useid;
    }

    public void setUseid(String useid) {
        this.useid = useid;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public PublishGood(String useid, String introduction, String title, String photos, String category, String price) {
        this.useid = useid;
        this.introduction = introduction;
        this.title = title;
        this.photos = photos;
        this.category = category;
        this.price = price;
    }

    @Override
    public String toString() {
        return "PublishGood{" +
                "useid='" + useid + '\'' +
                ", introduction='" + introduction + '\'' +
                ", title='" + title + '\'' +
                ", photos='" + photos + '\'' +
                ", category='" + category + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
