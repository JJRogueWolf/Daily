package com.aswarth.dailyApp;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BuyListHolder extends RecyclerView.ViewHolder {

    final ImageView mProductImage;
    final View myView;

    public BuyListHolder(@NonNull final View itemView) {
        super(itemView);
        this.myView = itemView;
        this.mProductImage = itemView.findViewById(R.id.list_image);
    }

}
