package com.example.android.util;

public class User {
    private String user_ID;
    private String password;
    private String nick_NAME;
    private String photo;
    private String sex;
    private String introduction;

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
