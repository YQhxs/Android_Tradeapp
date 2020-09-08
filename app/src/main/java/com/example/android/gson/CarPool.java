package com.example.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Description: 定义 获取服务端全部拼车数据接口的gson解析的对象
 * @Author: YQHxs
 * @Date: Created in 2020/5/3 18:06
 * @Modified By:
 */
public class CarPool {
    @SerializedName("id")
    private int carpoolId;
    @SerializedName("user_ID")
    private String userId;
    @SerializedName("nick_NAME")
    private String nickName;
    @SerializedName("photo")
    private String avatar;
    private String content;


    public int getCarpoolId() {
        return carpoolId;
    }

    public void setCarpoolId(int carpoolId) {
        this.carpoolId = carpoolId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CarPool{" +
                "carpoolId=" + carpoolId +
                ", userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
