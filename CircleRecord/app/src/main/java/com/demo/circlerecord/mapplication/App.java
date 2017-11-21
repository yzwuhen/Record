package com.demo.circlerecord.mapplication;

import android.app.Application;

/**
 * Created by yz_wuhen on 2017/11/21.
 */

public class App extends Application {
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}
