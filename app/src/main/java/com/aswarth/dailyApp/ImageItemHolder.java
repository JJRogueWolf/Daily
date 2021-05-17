package com.aswarth.dailyApp;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.droidsonroids.gif.GifImageView;

public class ImageItemHolder extends RecyclerView.ViewHolder {

    final GifImageView mProductImage;
    final View myView;

    public ImageItemHolder(@NonNull final View itemView) {
        super(itemView);
        this.myView = itemView;
        this.mProductImage = itemView.findViewById(R.id.list_image);
    }

}
