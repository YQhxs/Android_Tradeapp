package com.example.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Description:
 * @Author: YQHxs
 * @Date: Created in 2020/5/19 11:06
 * @Modified By:
 */
public class PublishCarPool {

    private String content;
    @SerializedName("user_ID")
    private String userid;

    public PublishCarPool() {
    }

    public PublishCarPool( String content, String userid) {
        this.content = content;
        this.userid = userid;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "PublishCarPool{" +
                ", content='" + content + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}
