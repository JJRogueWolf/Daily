package com.aswarth.daily;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView homeNavi, buyNavi, cloneNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeNavi = findViewById(R.id.home_navi);
        buyNavi = findViewById(R.id.buy_navi);
        cloneNavi = findViewById(R.id.clone_navi);
        homeNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new HomeFragment());
            }
        });

        buyNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new BuyFragment());
            }
        });

        cloneNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new CloneFragment());
            }
        });
    }

    private void changeFragment(Fragment fragment){
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, fragment).commit();
    }
}
