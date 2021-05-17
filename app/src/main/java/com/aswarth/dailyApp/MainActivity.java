package com.aswarth.dailyApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.aswarth.dailyApp.AppController.allItems;
import static com.aswarth.dailyApp.AppController.buyItems;
import static com.aswarth.dailyApp.AppController.buyListCount;
import static com.aswarth.dailyApp.AppController.cloneItems;
import static com.aswarth.dailyApp.AppController.cloned_user_id;
import static com.aswarth.dailyApp.AppController.homeItems;
import static com.aswarth.dailyApp.AppController.mainActivityActions;

public class MainActivity extends AppCompatActivity implements MainActivityActions {
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1001;
    public final static int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 2002;
    public final static int GALLERY_IMAGE_QR_SCAN_REQUEST_CODE = 808;
    public final static int CAMERA_QR_SCAN_REQUEST_CODE = 909;
    private static final int CAMERA_REQUEST = 111;

    final List<String> listPermissionsNeeded = new ArrayList<>();

    TextView homeNavi, buyNavi, loadingText;
    ImageView cloneNaviIcon, homeNaviIcon, shopNaviIcon;
    ImageView addItem, qrCodeScanner, qrCodeGenerator;
    Activity activity;
    SessionManager sessionManager;
    AdView ads;
    RelativeLayout loadingScreen;
    Button refreshCloneButton;

    HomeFragment homeFragment;
    CloneFragment cloneFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        activity = this;
        AppController.mainActivityActions = this;
        sessionManager = new SessionManager(this);
        homeFragment = new HomeFragment();
        cloneFragment = new CloneFragment();
        homeNavi = findViewById(R.id.home_navi);
        buyNavi = findViewById(R.id.buy_navi);
        cloneNaviIcon = findViewById(R.id.clone_navi);
        homeNaviIcon = findViewById(R.id.home_navi_icon);
        shopNaviIcon = findViewById(R.id.shop_navi_icon);
        addItem = findViewById(R.id.add_item);
        buyListCount = findViewById(R.id.shop_count);
        loadingScreen = findViewById(R.id.loading_screen);
        loadingScreen.setVisibility(View.GONE);
        loadingText = findViewById(R.id.loading_text);
        qrCodeGenerator = findViewById(R.id.qr_code_generator);
        qrCodeScanner = findViewById(R.id.qr_code_scanner);
        refreshCloneButton = findViewById(R.id.refresh_button);
        refreshCloneButton.setVisibility(View.GONE);

        if (buyItems.size() == 0) {
            buyListCount.setVisibility(View.GONE);
        } else {
            buyListCount.setVisibility(View.VISIBLE);
            buyListCount.setText(String.valueOf(buyItems.size()));
        }
        if (cloneItems.isEmpty()){
            hideCloneNavi();
        } else {
            showCloneNavi();
        }

        cloneNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.not_selected)));
        homeNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
        shopNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.not_selected)));

        ads = findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder().build();
        ads.loadAd(request);
        ads.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });

        homeNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCloneButton.setVisibility(View.GONE);
                cloneNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.not_selected)));
                homeNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                shopNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.not_selected)));
                if (cloneItems.isEmpty()){
                    hideCloneNavi();
                } else {
                    showCloneNavi();
                }
                changeFragment(homeFragment);
            }
        });

        buyNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCloneButton.setVisibility(View.GONE);
                cloneNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.not_selected)));
                homeNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.not_selected)));
                shopNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                if (cloneItems.isEmpty()){
                    hideCloneNavi();
                } else {
                    showCloneNavi();
                }
                changeFragment(new BuyFragment());
            }
        });

        cloneNaviIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCloneButton.setVisibility(View.VISIBLE);
                cloneNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                homeNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.not_selected)));
                shopNaviIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.not_selected)));
                changeFragment(cloneFragment);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermissions()) {
                    selectImage();
                }
            }
        });

        qrCodeGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrGeneratorIntent = new Intent(getApplicationContext(), QRGenerator.class);
                startActivity(qrGeneratorIntent);
            }
        });

        qrCodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermissions()){
                    selectQrScanner();
                }
            }
        });

        refreshCloneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadClone();
            }
        });
    }

    private boolean checkCameraPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), CAMERA_REQUEST);
            return false;
        }
        return true;
    }

    private void selectImage() {
        final CharSequence[] options = {"Take photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (options[item].equals("Take photo")) {
                    captureImage();
                } else if (options[item].equals("Choose from gallery")) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
                } else if (options[item].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectQrScanner() {
        final CharSequence[] options = {"Read from camera", "Choose from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Scan QR Code");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (options[item].equals("Read from camera")) {
                    GoToQRScanner();
                } else if (options[item].equals("Choose from gallery")) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, GALLERY_IMAGE_QR_SCAN_REQUEST_CODE);
                } else if (options[item].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void GoToQRScanner() {
        Intent intent = new Intent(getApplicationContext(), QRCodeScanner.class);
//        startActivity(intent);
        startActivityForResult(intent, CAMERA_QR_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    uploadImage(getImageUri(getApplicationContext(), photo));
//                    allItems.add(0, new Items(getApplicationContext(),getImageUri(getApplicationContext(), photo)));
//                    homeItems.add(0, new Items(getApplicationContext(),getImageUri(getApplicationContext(), photo)));
//                    Gson gson = new Gson();
//                    sessionManager.setItemList(gson.toJson(allItems));
//                    homeFragment.setupItems();
                }
            }
        } else if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                uploadImage(data.getData());
//                allItems.add(0, new Items(getApplicationContext(),data.getData()));
//                homeItems.add(0, new Items(getApplicationContext(),data.getData()));
//                Gson gson = new Gson();
//                sessionManager.setItemList(gson.toJson(allItems));
//                homeFragment.setupItems();
            }
        }

        if (requestCode == CAMERA_QR_SCAN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                loadClone();
            }
        }

        if (requestCode == GALLERY_IMAGE_QR_SCAN_REQUEST_CODE){
            if (resultCode == RESULT_OK && data != null) {
                try {
                    showLoading("Loading Clone Data");
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    int[] intArray = new int[bitmap.getWidth()*bitmap.getHeight()];
                    bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Reader reader = new MultiFormatReader();
                    Result result = reader.decode(binaryBitmap);
                    cloned_user_id = result.getText();
                    sessionManager.setClonedUserId(cloned_user_id);
                    Log.i("ScanResult", cloned_user_id);
                    loadClone();
                } catch (Exception e) {
                    hideLoading();
                    e.printStackTrace();
                    Toast.makeText(activity, "QR Code Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void uploadImage(final Uri image) {
        mainActivityActions.showLoading("Adding Image To Your Collection");
        long tsLong = System.currentTimeMillis() / 1000;
        String imageName = "IMG_" + String.valueOf(tsLong) + ".jpg";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference().child("images/" + imageName);
        ref.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                allItems.add(0, new Items(image, uri.toString()));
                                homeItems.add(0, new Items(image, uri.toString()));
                                Gson gson = new Gson();
                                sessionManager.setItemList(gson.toJson(allItems));
                                mainActivityActions.hideLoading();
                                changeFragment(homeFragment);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mainActivityActions.hideLoading();
                        e.printStackTrace();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.clear();
            } else {
                Toast.makeText(this, "Permissions Denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadClone(){
        if (!cloned_user_id.isEmpty()){
            showLoading("Refreshing Data");
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(cloned_user_id);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cloneItems = new ArrayList<>();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        int count = 0;
                        for (Items item : cloneItems){
                            if (item.getImageUrl().equals(dataSnapshot.getValue().toString().replace("url=","").replace("{","").replace("}",""))){
                                count++;
                            }
                        }
                        if (count == 0) {
                            cloneItems.add(new Items(dataSnapshot.getValue().toString().replace("url=", "").replace("{", "").replace("}", "")));
                        }
                    }
                    if (cloneNaviIcon.getVisibility() == View.VISIBLE){
                        cloneFragment.setUpCloneAdapter();
                    }
                    if (cloneItems.isEmpty()){
                        hideCloneNavi();
                    } else {
                        showCloneNavi();
                    }
                    hideLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hideLoading();
                }
            });
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        long tsLong = System.currentTimeMillis() / 1000;
        String ts = String.valueOf(tsLong);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ts, null);
        return Uri.parse(path);
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void showLoading(String text) {
        loadingText.setText(text);
        loadingScreen.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingScreen.setVisibility(View.GONE);
    }

    @Override
    public void hideCloneNavi() {
        cloneNaviIcon.setVisibility(View.GONE);
    }

    @Override
    public void showCloneNavi() {
        cloneNaviIcon.setVisibility(View.VISIBLE);
    }
}
