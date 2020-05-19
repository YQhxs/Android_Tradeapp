package com.example.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Images {
    @SerializedName("name")
    private List<String> imagespath;
    private String photos = "";

    public Images(List<String> imagespath) {
        this.imagespath = imagespath;
    }

    public List<String> getimagespath() {
        return imagespath;
    }

    public void setimagespath(List<String> imagespath) {
        this.imagespath = imagespath;
    }

    public String getPhotos() {
//        未细想没有照片时情况
        if (imagespath.size() > 0) {
            photos = imagespath.get(0);
            for (int i = 1; i < imagespath.size(); i++) {
                photos = photos.concat("**" + imagespath.get(i));
            }
        }
        return photos;
    }

    @Override
    public String toString() {
        return "Images{" +
                "imagespath=" + imagespath +
                '}';
    }
}
