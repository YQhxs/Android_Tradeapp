package com.example.android.util;

import android.app.Application;
import android.content.Context;
//获取上下文context对象
public class GetContext extends Application {
    private static Context context;
    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
