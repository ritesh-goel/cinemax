package com.rg.moviescentral.network;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.rg.moviescentral.MyApplication;

/**
 * Created by Ritesh Goel on 11-10-2016.
 */

public class VolleySingleton {

    private static VolleySingleton sInstance = null;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private VolleySingleton(){
        requestQueue = Volley.newRequestQueue(MyApplication.getContext());
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {

            private LruCache<String,Bitmap> bitmapLruCache = new LruCache<>((int)(Runtime.getRuntime().maxMemory()/1024)/8);

            @Override
            public Bitmap getBitmap(String url) {
                return bitmapLruCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                bitmapLruCache.put(url,bitmap);
            }
        });
    }

    public static VolleySingleton getInstance(){
        if(sInstance == null){
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }
    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
    public ImageLoader getImageLoader(){
        return imageLoader;
    }
}
