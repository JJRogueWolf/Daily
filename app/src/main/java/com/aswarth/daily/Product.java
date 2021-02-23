package com.aswarth.daily;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Product {
    private Uri image;
    private String imageUrl;
    FirebaseStorage storage;
    StorageReference storageReference;

    public Product(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Product(Uri image) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        this.image = image;
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
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
