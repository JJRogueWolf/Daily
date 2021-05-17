package com.aswarth.dailyApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.aswarth.dailyApp.AppController.BitMapToString;
import static com.aswarth.dailyApp.AppController.allItems;

public class Product {
    private String capturedImage = null;
    private String imageUrl = "";
    private String imageBitmapString = "";
    private Bitmap imageBitmap = null;

    public Product(Context context, String imageUrl) {
        this.imageUrl = imageUrl;
//        this.capturedImage = null;
        if (imageBitmapString == null) {
            getBitmapFromURL(context, imageUrl);
        }
    }

    public Product(Context context, @Nullable Uri capturedImage, Bitmap imageBitmap) {
        setImageBitmap(imageBitmap);
        this.imageBitmapString = BitMapToString(imageBitmap);
        this.capturedImage = capturedImage.toString();
        this.imageUrl = "";
    }

    public Uri getCapturedImage(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        this.imageUrl = "";
        try {
            this.imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(capturedImage));
            this.imageBitmapString = AppController.BitMapToString(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long tsLong = System.currentTimeMillis()/1000;
        String imageName = "IMG_" +  String.valueOf(tsLong);
        if (capturedImage != null) {
            uploadImage(sessionManager, storageReference, Uri.parse(capturedImage), imageName);
        }
        return Uri.parse(capturedImage);
    }

    public void setCapturedImage(Uri capturedImage) {
        this.capturedImage = capturedImage.toString();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public JSONObject getJSONData() throws JSONException {
        JSONObject parameters = new JSONObject();
        parameters.put("imageUrl", getImageUrl());
        return parameters;
    }

    private void uploadImage(final SessionManager sessionManager, StorageReference storageReference, Uri filePath, String imageName) {
        StorageReference ref = storageReference.child("images/" + imageName);
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                setImageUrl(uri.toString());
//                                setCapturedImage(null);
                                Gson gson = new Gson();
//                                for(Product product : allItems){
//                                    product.setImageBitmap(null);
//                                }
                                sessionManager.setItemList(gson.toJson(allItems));
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });
    }

    public void getBitmapFromURL(Context context, String src) {
        try {
            final SessionManager sessionManager = new SessionManager(context);
            Picasso.get().load(src).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    setImageBitmap(bitmap);
                    setImageBitmapString(BitMapToString(bitmap));
                    Gson gson = new Gson();
//                    for(Product product : allItems){
//                        product.setImageBitmap(null);
//                    }
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

    public String getImageBitmapString() {
        return imageBitmapString;
    }

    public void setImageBitmapString(String imageBitmapString) {
        this.imageBitmapString = imageBitmapString;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
