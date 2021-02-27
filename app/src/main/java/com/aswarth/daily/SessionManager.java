package com.aswarth.daily;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    final SharedPreferences ref;
    final SharedPreferences.Editor editor;
    final Context _context;
    final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SessionManager";

    public static final String USER_ID = "user_id";
    public static final String ITEMS = "items";
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        ref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = ref.edit();
    }

    public void setItemList(String items){
        editor.putString(ITEMS, items);
        editor.commit();
    }

    public String getItems(){
        return ref.getString(ITEMS, "");
    }

    public String getUserId(){
        return ref.getString(USER_ID, "");
    }

    public void setUserId(String userId){
        editor.putString(USER_ID, userId);
        editor.commit();
    }
}
