package com.aswarth.daily;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1001;

    TextView homeNavi, buyNavi, cloneNavi;
    ImageView addItem;
    Activity activity;
    String mCapturedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        homeNavi = findViewById(R.id.home_navi);
        buyNavi = findViewById(R.id.buy_navi);
        cloneNavi = findViewById(R.id.clone_navi);
        addItem = findViewById(R.id.add_item);
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

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1100);
                } else {
                    captureImage();
                }
            }
        });
    }

    private void captureImage(){
//        File file = new File(Environment.getExternalStorageDirectory()
//                .getAbsolutePath() + "/MyFolder", "myImage"+ ".jpg");
//
//        mCapturedImagePath = file.getAbsolutePath();
//        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    AppController.allItems.add(new Product(getImageUri(getApplicationContext(), photo)));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(this, "Need Camera Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        long tsLong = System.currentTimeMillis()/1000;
        String ts = String.valueOf(tsLong);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ts, null);
        return Uri.parse(path);
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, fragment).commit();
    }
}
