package com.aswarth.daily;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import static com.aswarth.daily.AppController.allItems;

public class Product {
    private Uri image;
    private String imageUrl;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String imageName;
    private final SessionManager sessionManager;

    public Product(Context context, String imageUrl) {
        sessionManager = new SessionManager(context);
        this.imageUrl = imageUrl;
    }

    public Product(Context context, Uri image) {
        sessionManager = new SessionManager(context);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        this.image = image;
        long tsLong = System.currentTimeMillis()/1000;
        imageName = "IMG_" +  String.valueOf(tsLong);
        if (image != null) {
            uploadImage(image);
        }
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
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

    private void uploadImage(Uri filePath) {
        StorageReference ref = storageReference.child("images/" + imageName);
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                setImageUrl(uri.toString());
                                setImage(null);
                                Gson gson = new Gson();
                                sessionManager.setItemList(gson.toJson(allItems));
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });
    }
}
