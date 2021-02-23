package com.aswarth.daily;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageListHolder extends RecyclerView.ViewHolder {

    final ImageView mProductImage;
    final View myView;

    public ImageListHolder(@NonNull final View itemView) {
        super(itemView);
        this.myView = itemView;
        this.mProductImage = itemView.findViewById(R.id.list_image);
    }

}
