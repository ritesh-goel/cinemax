package com.rg.moviescentral;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;

/**
 * Created by Ritesh Goel on 11-10-2016.
 */

public class MyApplication extends Application {

    private static MyApplication sInstance;
    public static final String API_KEY_TMDB = "64331fe3baac2e831d295e21081853f3";
    public static final String YOUTUBE_API_KEY = "AIzaSyB0-ehy7VelgXZybMssT79kaMoIFTUuujY";
    public static final String ROTTEN_TOMATO_KEY = "xgadxfmx9gpynmz3rea99qzn"; // cinematics app api key

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-5000638620408100~4989126873");
    }
    public static MyApplication getInstance(){
        return sInstance;
    }
    public static Context getContext(){
        return sInstance.getApplicationContext();
    }
}
