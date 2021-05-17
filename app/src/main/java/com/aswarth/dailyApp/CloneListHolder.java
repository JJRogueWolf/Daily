package com.aswarth.dailyApp;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.droidsonroids.gif.GifImageView;

public class CloneListHolder extends RecyclerView.ViewHolder {

    final GifImageView mProductImage;
    final View myView;

    public CloneListHolder(@NonNull final View itemView) {
        super(itemView);
        this.myView = itemView;
        this.mProductImage = itemView.findViewById(R.id.list_image);
    }

}
