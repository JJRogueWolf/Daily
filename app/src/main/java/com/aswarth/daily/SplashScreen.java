package com.aswarth.daily;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;

import static com.aswarth.daily.AppController.allItems;

public class SplashScreen extends AppCompatActivity {
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(this);
        if (sessionManager.getUserId().isEmpty()){
            sessionManager.setUserId(Build.ID + Build.USER + Build.DISPLAY);
        }
        AppController.user_id = sessionManager.getUserId();


        allItems = new ArrayList<>();
        allItems.add(new Product("https://m.media-amazon.com/images/I/91o0m2iIpVL._AC_UL320_.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/518GEoZtbhL.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/4192htT55fL.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/71vB-g08DoL._SY741_.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/81tiRzUBKEL._SL1500_.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/81JmTiQA68L._SL1500_.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/61szrCRWOEL._SL1000_.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/41YDq-tchUL.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/61WGQvZPyRL._SL1000_.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/61EsE7y9mHL._SL1200_.jpg"));
        allItems.add(new Product("https://images-na.ssl-images-amazon.com/images/I/61Zgf3krb7L._SL1000_.jpg"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent endSplashIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(endSplashIntent);
                finish();
            }
        }, 3000);
    }
}
