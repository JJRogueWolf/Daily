package com.aswarth.daily;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    private Uri image;
    private String imageUrl;

    public Product(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public Product(Uri image){
        this.image = image;
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
        parameters.put( "imageUrl", getImageUrl());
        return parameters;
    }
}
