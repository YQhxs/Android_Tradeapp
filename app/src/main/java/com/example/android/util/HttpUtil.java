package com.example.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil  {
    private static Request req;
    public static void sendOkHttpRequest(Request request,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();

        req = request;
        client.newCall(req).enqueue(callback);
    }

}
