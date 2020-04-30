package com.example.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tradeinfo {
    @SerializedName("data")
    private List<TradeGoods> tradeInfo;

    public Tradeinfo(List<TradeGoods> tradeInfo) {
        this.tradeInfo = tradeInfo;
    }

    public List<TradeGoods> getTradeInfo() {
        return tradeInfo;
    }

    public void setTradeInfo(List<TradeGoods> tradeInfo) {
        this.tradeInfo = tradeInfo;
    }
}
