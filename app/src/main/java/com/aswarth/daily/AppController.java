package com.aswarth.daily;

import android.app.Application;

import java.util.ArrayList;


public class AppController extends Application {
    private static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    public static String user_id = "";

    public static ArrayList<Product> allItems = null;
    public static ArrayList<Product> buyItems = null;
    public static ArrayList<Product> cloneItems = null;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}

