package com.aswarth.daily;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.aswarth.daily.AppController.buyItems;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListHolder> {
    private final ArrayList<Product> products;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    long lastClickTime = 0;

    public ImageListAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ImageListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ImageListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageListHolder holder, final int position) {
        if (!products.get(position).getImageUrl().isEmpty()){
            Picasso.get().load(products.get(position).getImageUrl()).into(holder.mProductImage);
        } else {
            holder.mProductImage.setImageURI(products.get(position).getImage());
        }
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                    if (buyItems == null){
                        buyItems = new ArrayList<>();
                    }
                    buyItems.add(products.get(position));
                    lastClickTime = 0;
                    return;
                }
                lastClickTime = clickTime;
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
