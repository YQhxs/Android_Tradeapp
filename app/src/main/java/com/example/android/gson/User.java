package com.example.android.gson;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable{
    private int id;
    private String user_ID;
    private String password;
    private String nick_NAME;
    private String photo;
    private String sex;

    private String introduction;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", user_ID='" + user_ID + '\'' +
                ", password='" + password + '\'' +
                ", nick_NAME='" + nick_NAME + '\'' +
                ", photo='" + photo + '\'' +
                ", sex='" + sex + '\'' +
                ", introduction='" + introduction + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick_NAME() {
        return nick_NAME;
    }

    public void setNick_NAME(String nick_NAME) {
        this.nick_NAME = nick_NAME;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }


}
