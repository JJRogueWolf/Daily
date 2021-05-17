package com.aswarth.dailyApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class QRCodeScanner extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView codeScannerView;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_w_r_code_scanner);
        sessionManager = new SessionManager(getApplicationContext());
        codeScannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, codeScannerView);
        codeScanner.startPreview();
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                AppController.cloned_user_id = result.getText();
                sessionManager.setClonedUserId(AppController.cloned_user_id);
                Log.i("ScanResult", AppController.cloned_user_id);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}