package com.aswarth.dailyApp;


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
    public static final String CLONED_USER_ID = "clone_user_id";
    public static final String ITEMS = "items";
    public static final String BUY_ITEMS = "buy_items";
    public static final String CLONE_LIST = "clone_list";
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

    public void setBuyItemList(String items){
        editor.putString(BUY_ITEMS, items);
        editor.commit();
    }

    public void setCloneList(String items){
        editor.putString(CLONE_LIST, items);
        editor.commit();
    }

    public String getItems(){
        return ref.getString(ITEMS, "");
    }
    public String getBuyItems(){
        return ref.getString(BUY_ITEMS, "");
    }
    public String getCloneList(){
        return ref.getString(CLONE_LIST, "");
    }
    public String getUserId(){
        return ref.getString(USER_ID, "");
    }

    public void setUserId(String userId){
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public String getClonedUserId(){
        return ref.getString(CLONED_USER_ID, "");
    }

    public void setClonedUserId(String userId){
        editor.putString(CLONED_USER_ID, userId);
        editor.commit();
    }
}
