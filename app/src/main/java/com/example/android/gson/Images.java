package com.example.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Images {
    @SerializedName("name")
    private List<String> iamgespath;

    public Images(List<String> iamgespath) {
        this.iamgespath = iamgespath;
    }

    public List<String> getIamgespath() {
        return iamgespath;
    }

    public void setIamgespath(List<String> iamgespath) {
        this.iamgespath = iamgespath;
    }
}
