package com.example.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Description:
 * @Author: YQHxs
 * @Date: Created in 2020/5/18 10:22
 * @Modified By:
 */
public class CarPoolInfo {
    @SerializedName("data")
    private List<CarPool> carPools;

    public CarPoolInfo(List<CarPool> carPools) {
        this.carPools = carPools;
    }

    public List<CarPool> getCarPools() {
        return carPools;
    }

    public void setCarPools(List<CarPool> carPools) {
        this.carPools = carPools;
    }
}
