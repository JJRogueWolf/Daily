package com.aswarth.dailyApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.aswarth.dailyApp.AppController.allItems;
import static com.aswarth.dailyApp.AppController.buyItems;
import static com.aswarth.dailyApp.AppController.cloneItems;
import static com.aswarth.dailyApp.AppController.cloned_user_id;
import static com.aswarth.dailyApp.AppController.homeItems;
import static com.aswarth.dailyApp.AppController.user_id;

public class SplashScreen extends AppCompatActivity {
    private final int MY_REQUEST_CODE = 110011;
    SessionManager sessionManager;
    RelativeLayout splashLoading;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(this);
        splashLoading = findViewById(R.id.splash_loading);
        splashLoading.setVisibility(View.GONE);
        if (sessionManager.getUserId().isEmpty()) {
            sessionManager.setUserId(UUID.randomUUID().toString());
        }

        AppController.user_id = sessionManager.getUserId();
        if (!sessionManager.getClonedUserId().isEmpty()) {
            AppController.cloned_user_id = sessionManager.getClonedUserId();
        } else {
            AppController.cloned_user_id = "";
        }
        allItems = new ArrayList<>();
        homeItems = new ArrayList<>();
        buyItems = new ArrayList<>();
        cloneItems = new ArrayList<>();

        if (!sessionManager.getItems().isEmpty()) {
            Gson gson = new Gson();
            allItems = gson.fromJson(sessionManager.getItems(), new TypeToken<List<Items>>() {
            }.getType());
        } else {
            initializeList();
        }

        if (!sessionManager.getBuyItems().isEmpty()) {
            Gson gson = new Gson();
            buyItems = gson.fromJson(sessionManager.getBuyItems(), new TypeToken<List<Items>>() {
            }.getType());
            LoadBuy();
        }

        if (!cloned_user_id.isEmpty()) {
            LoadClone();
        }else {
            checkForAppUpdate();
        }
    }

    private void checkForAppUpdate(){
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(result -> {
            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            result,
                            AppUpdateType.IMMEDIATE,
                            SplashScreen.this,
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    goToMainPage();
                }
            } else {
                goToMainPage();
            }
        });

        appUpdateInfoTask.addOnFailureListener(e -> {
            e.printStackTrace();
            goToMainPage();
        });

//        appUpdateInfoTask.addOnCompleteListener(task -> {
//            goToMainPage();
//        });
    }

    private void goToMainPage() {
        homeItems = new ArrayList<>();
        homeItems.addAll(allItems);
        if (!buyItems.isEmpty()) {
            for (Items item : allItems) {
                for (Items bItem : buyItems) {
                    if (item.getImageUri().toString().equals(bItem.getImageUri().toString())) {
                        homeItems.remove(item);
                    }
                }
            }
        }
        firebaseDatabase = null;
        databaseReference = null;
        Intent endSplashIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(endSplashIntent);
        finish();
    }

    private void LoadBuy() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(user_id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Items> list = new ArrayList<>(buyItems);
                for (Items item : list) {
                    int count = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getValue().toString().replace("url=", "").replace("{", "").replace("}", "").equals(item.getImageUrl())) {
                            count++;
                        }
                    }
                    if (count == 0) {
                        for (Items items : buyItems) {
                            if (items.getImageUrl().equals(item.getImageUrl())) {
                                buyItems.remove(items);
                                break;
                            }
                        }
                    }
                }
                Log.i("tt", "ugu");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                splashLoading.setVisibility(View.GONE);
                Toast.makeText(SplashScreen.this, "Something went wrong!! Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadClone() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(cloned_user_id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Items> list = new ArrayList<>(cloneItems);
                for (Items item : list) {
                    int count = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getValue().toString().replace("url=", "").replace("{", "").replace("}", "").equals(item.getImageUrl())) {
                            count++;
                        }
                    }
                    if (count == 0) {
                        for (Items items : cloneItems) {
                            if (items.getImageUrl().equals(item.getImageUrl())) {
                                cloneItems.remove(items);
                                break;
                            }
                        }
                    }
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int count = 0;
                    for (Items item : cloneItems) {
                        if (item.getImageUrl().equals(dataSnapshot.getValue().toString().replace("url=", "").replace("{", "").replace("}", ""))) {
                            count++;
                        }
                    }
                    if (count == 0) {
                        cloneItems.add(new Items(dataSnapshot.getValue().toString().replace("url=", "").replace("{", "").replace("}", "")));
                    }
                }
                databaseReference.removeEventListener(this);
//                cloneUriToUrl(0);
                checkForAppUpdate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                splashLoading.setVisibility(View.GONE);
                goToMainPage();
                Toast.makeText(SplashScreen.this, "Something went wrong!! Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cloneUriToUrl(int count) {
        downloadURL(count);
    }

    private void downloadURL(final int count) {
        try {
            if (cloneItems.get(count).getImageUri().toString().isEmpty()) {
                Picasso.get().load(cloneItems.get(count).getImageUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        cloneItems.get(count).setImageUri(getImageUri(getApplicationContext(), bitmap));
                        Gson gson = new Gson();
                        sessionManager.setCloneList(gson.toJson(cloneItems));

                        if (count == cloneItems.size() - 1) {
                            goToMainPage();
                        }
                        if (count < cloneItems.size() - 1) {
                            Picasso.get().cancelRequest(this);
                            cloneUriToUrl(count + 1);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        e.printStackTrace();
                        splashLoading.setVisibility(View.GONE);
                        Toast.makeText(SplashScreen.this, "Something went wrong!! Please try again later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            } else {
                if (count == cloneItems.size() - 1) {
                    goToMainPage();
                }
                if (count < cloneItems.size() - 1) {
                    cloneUriToUrl(count + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            splashLoading.setVisibility(View.GONE);
            Toast.makeText(SplashScreen.this, "Something went wrong!! Please try again later", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                goToMainPage();
            }
        }
    }

    private void initializeList() {
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image001), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image001.jpg?alt=media&token=91c9930c-e944-460e-8d29-2792fa38ae4e"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image002), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image002.jpg?alt=media&token=cf18ebdd-b353-48ef-9336-67d564ad2f6b"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image003), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image003.jpg?alt=media&token=c0c88728-9810-47a9-8c97-6c1898a186ec"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image004), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image004.jpg?alt=media&token=d7892a95-1e21-4a89-85cc-49cab5c87f3f"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image005), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image005.jpg?alt=media&token=789cbfad-e1c7-4e55-ae33-bab159697b9a"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image006), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image006.jpg?alt=media&token=0fbe4c24-ed1c-4352-acec-b2f9a45f36f9"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image007), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image007.jpg?alt=media&token=313058c3-b99b-49fe-a8e8-397b2306f315"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image008), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image008.jpg?alt=media&token=c5180fa8-b5b1-4a7f-b4c2-c5f8959d8f6e"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image009), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image009.jpg?alt=media&token=9a9de001-1de6-46a4-b38d-9f5070c4e891"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image010), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image010.jpg?alt=media&token=1682aeba-5794-4a52-bb61-6fea86d7b7ec"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image011), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image011.jpg?alt=media&token=9fa0aa46-e397-4ad5-a8e4-b444672d2ffe"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image012), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image012.jpg?alt=media&token=b4bb005c-5756-4f07-9628-bb89e0851f3c"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image013), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image013.jpg?alt=media&token=e3628f8a-48a7-4e9b-93d5-56b9a389d4c2"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image014), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image014.jpg?alt=media&token=e8aabe67-1d1a-4a1f-a49e-0dab2666e640"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image015), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image015.jpg?alt=media&token=3e84a139-f273-43ca-b32a-b025f3bf0af4"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image016), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image016.jpg?alt=media&token=41493da9-5339-4fca-b5eb-35c5cc5f705b"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image017), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image017.jpg?alt=media&token=dade5e06-1031-4e2d-bf4b-283378fb4720"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image018), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image018.jpg?alt=media&token=22fd954f-2423-433e-b8f4-ab17a3410d11"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image019), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image019.jpg?alt=media&token=abda58e2-8d24-466d-b87c-0306fb234563"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image020), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image020.jpg?alt=media&token=db72e3c5-5af5-455f-8d6f-5e156989668d"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image021), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image021.jpg?alt=media&token=566ed9c0-9728-4d21-aacf-26eb94bf5c77"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image022), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image022.jpg?alt=media&token=6bc955eb-27d5-434b-8e10-9b5b024557ef"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image023), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image023.jpg?alt=media&token=61551bb3-fd9e-4b95-a1cd-bc65239d8833"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image024), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image024.jpg?alt=media&token=e223171e-d58a-4a1f-b6d9-e3503092bc72"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image025), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image025.jpg?alt=media&token=c0545d95-410d-4915-9500-9e7981d64594"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image026), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image026.jpg?alt=media&token=8620ef72-3760-4cbd-80d3-1f4d902b11fb"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image027), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image027.jpg?alt=media&token=9960c60f-d8cc-4c7e-935f-d70d8723e783"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image028), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image028.jpg?alt=media&token=8130230a-88bc-499d-b7ee-81bf05feb2b7"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image029), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image029.jpg?alt=media&token=8818d634-f0f1-438e-a14b-46884e85bf20"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image030), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image030.jpg?alt=media&token=7c775bba-cbef-4f81-9d8d-fff1e737631e"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image031), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image031.jpg?alt=media&token=3bee6804-bbb4-447f-b485-72b4d4f565f9"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image032), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image032.jpg?alt=media&token=aa3a75ea-87f6-486f-98e0-fe78aeda88a5"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image033), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image033.jpg?alt=media&token=ca8c5dd7-f057-40ea-a34b-87e0125d9a4f"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image034), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image034.jpg?alt=media&token=e388bb78-addd-4f6f-9b31-e0b8bf771d07"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image035), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image035.jpg?alt=media&token=3d3fff1d-8087-4d7d-b86f-034823f20006"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image036), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image036.jpg?alt=media&token=3a59584c-4522-4b89-950d-877ea50f7084"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image037), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image037.jpg?alt=media&token=df4f4813-8db8-47ec-8e82-b1e04b7afe70"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image038), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image038.jpg?alt=media&token=2bcff922-5102-4eba-8a9a-5b67be924ed7"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image039), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image039.jpg?alt=media&token=9ade3c40-bf38-413a-b091-e85c673fc885"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image040), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image040.jpg?alt=media&token=20a7c73b-c722-4503-931b-8b193a0cd976"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image041), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image041.jpg?alt=media&token=d3bf80e9-d046-4d36-b799-614efeeef6ea"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image042), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image042.jpg?alt=media&token=cf9a8b5f-54a9-4b2d-8966-f9ee6464e39c"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image043), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image043.jpg?alt=media&token=38d709d9-22ab-4c51-a433-b0dde19396ba"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image044), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image044.png?alt=media&token=33429658-3989-4d56-9cf3-98f811b09160"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image045), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image045.png?alt=media&token=ed40555d-6a46-4b6c-af36-fb3561f65bd2"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image046), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image046.png?alt=media&token=052b156b-b957-4bed-aff0-487564a601ac"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image047), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image047.jpg?alt=media&token=26f29704-13f2-4b32-8cd7-a6f82bcc03a3"));
        allItems.add(new Items(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image048), "https://firebasestorage.googleapis.com/v0/b/daily-412be.appspot.com/o/image048.jpg?alt=media&token=a909787e-e6a9-4005-b634-e9919550844b"));
    }
}
