package com.aswarth.dailyApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

import static com.aswarth.dailyApp.AppController.BitMapToString;
import static com.aswarth.dailyApp.AppController.allItems;

public class Items {
    private String imageUri = "";
    private String imageUrl = "";

    public Items(Context context, Uri imageUri){
        this.imageUri = imageUri.toString();
    }

    public Items(Uri imageUri, String imageUrl){
        this.imageUri = imageUri.toString();
        this.imageUrl = imageUrl;
    }

    public Items(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public Uri getImageUri() { return Uri.parse(imageUri); }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri.toString();
    }

    public void setImageUri(String imageUri) { this.imageUri = imageUri;}

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(Context context, String imageUrl) {
        this.imageUrl = imageUrl;
        if (imageUri.isEmpty()){
            getUriFromUrl(context, imageUrl);
        }
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void getUriFromUrl(final Context context, String src) {
        try {
            final SessionManager sessionManager = new SessionManager(context);
            Picasso.get().load(src).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    setImageUri(getImageUriFromBitmap(context, bitmap));
                    Gson gson = new Gson();
                    sessionManager.setItemList(gson.toJson(allItems));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    e.printStackTrace();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getImageUriFromBitmap(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        long tsLong = System.currentTimeMillis() / 1000;
        String ts = String.valueOf(tsLong);
        return MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ts, null);
    }
}
