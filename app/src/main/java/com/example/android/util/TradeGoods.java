package com.example.android.util;

public class TradeGoods {

    private String title;
    private int img_url;
    private String solder_nickname;
    private int solder_head;
    public TradeGoods(String title, int img_url, String solder_nickname, int solder_head){
        this.title = title;
        this.img_url = img_url;
        this.solder_nickname = solder_nickname;
        this.solder_head = solder_head;
    }
    public String getTitle() {
        return title;
    }
    public int getImg_url() {
        return img_url;
    }
    public String getSolder_nickname() {
        return solder_nickname;
    }
    public int getSolder_head() {
        return solder_head;
    }

}
