package com.aswarth.dailyApp;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.FileUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class AppController extends Application {
    private static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    public static String user_id = "";
    public static String cloned_user_id = "";

    public static ArrayList<Items> allItems = null;
    public static ArrayList<Items> homeItems = null;
    public static ArrayList<Items> buyItems = null;
    public static ArrayList<Items> cloneItems = null;
    public static TextView buyListCount = null;

    public static MainActivityActions mainActivityActions = null;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static String BitMapToString(Bitmap bitmap) {
        if (bitmap == null)
            return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap StringToBitmap(String base64String){
        if( base64String != null && !base64String.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    public static void storeInDB(String url){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(AppController.user_id).child(url.substring(url.lastIndexOf("?")+1)).child("url");
        ref.setValue(url);
    }

    public static void removeFromDB(final String url, String id){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(id).child(url.substring(url.lastIndexOf("?")+1)).child("url");
        ref.removeValue();
    }
}

