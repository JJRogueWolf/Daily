package com.aswarth.dailyApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGenerator extends AppCompatActivity {
    ImageView qrCodeImageView;
    Button shareButton;
    QRGEncoder qrgEncoder;

    final List<String> listPermissionsNeeded = new ArrayList<>();
    private static final int STORAGE_REQUEST = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generator);
        qrCodeImageView = findViewById(R.id.qr_code_image_view);
        shareButton = findViewById(R.id.share_qr_code);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;
        qrgEncoder = new QRGEncoder(AppController.user_id, null, QRGContents.Type.TEXT, dimen);
        try {
            qrCodeImageView.setImageBitmap(qrgEncoder.encodeAsBitmap());
        } catch (WriterException e) {
            e.printStackTrace();
        }

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermissions()) {
                    share();
                }
            }
        });
    }

    private void share(){
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Bitmap bitmap = getScreenShot(rootView);
        shareImage(store(bitmap, "/screenshot.png"));
    }

    private boolean checkCameraPermissions() {
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), STORAGE_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.clear();
                share();
            } else {
                Toast.makeText(this, "Permissions Denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void shareImage(File file) {
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.aswarth.dailyApp", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Get My shop items by scanning this qr code!!! ");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
            Log.e("Error", e.toString());
        }
    }

    private File store(Bitmap bm, String fileName) {
        String dirPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/Screenshots";
        File dir = new File(dirPath);
        File screenshotFile;
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            screenshotFile = new File(dir, fileName);
            screenshotFile.createNewFile();
//             if (screenshotFile.exists ()) screenshotFile.delete ();
//            screenshotFile.mkdirs();

            FileOutputStream fOut = new FileOutputStream(screenshotFile);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();

            return screenshotFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}