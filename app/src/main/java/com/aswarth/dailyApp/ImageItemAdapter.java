package com.aswarth.dailyApp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.aswarth.dailyApp.AppController.allItems;
import static com.aswarth.dailyApp.AppController.buyItems;
import static com.aswarth.dailyApp.AppController.buyListCount;
import static com.aswarth.dailyApp.AppController.homeItems;
import static com.aswarth.dailyApp.AppController.mainActivityActions;
import static com.aswarth.dailyApp.AppController.storeInDB;

public class ImageItemAdapter extends RecyclerView.Adapter<ImageItemHolder> {
    private final ArrayList<Items> products;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    long lastClickTime = 0;
    SessionManager sessionManager;

    public ImageItemAdapter(Context context, ArrayList<Items> products) {
        sessionManager = new SessionManager(context);
        this.products = products;
    }

    @NonNull
    @Override
    public ImageItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ImageItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageItemHolder holder, final int position) {
        if (products.get(position).getImageUrl().isEmpty()) {
            holder.mProductImage.setImageURI(products.get(position).getImageUri());
        } else {
            Glide.with(holder.myView.getContext()).load(products.get(position).getImageUrl()).placeholder(holder.myView.getContext().getResources().getDrawable(R.drawable.placeholder)).into(holder.mProductImage);
        }
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    if (buyItems == null) {
                        buyItems = new ArrayList<>();
                    }
                    storeInDB(products.get(position).getImageUrl());
                    buyItems.add(products.get(position));
                    homeItems.remove(products.get(position));
                    notifyDataSetChanged();
                    Gson gson = new Gson();
                    sessionManager.setItemList(gson.toJson(allItems));
                    sessionManager.setBuyItemList(gson.toJson(buyItems));
                    if(buyItems.size() == 0){
                        buyListCount.setVisibility(View.GONE);
                    } else {
                        buyListCount.setVisibility(View.VISIBLE);
                        buyListCount.setText(String.valueOf(buyItems.size()));
                    }
                    lastClickTime = 0;
                    Toast.makeText(holder.myView.getContext(), "Item added to Shop", Toast.LENGTH_SHORT).show();
                    return;
                }
                lastClickTime = clickTime;
            }
        });

        holder.myView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                for (Items item : allItems) {
                    if (item.getImageUri().toString().equals(products.get(position).getImageUri().toString())) {
                        allItems.remove(item);
                        break;
                    }
                }
                homeItems.remove(position);
                notifyDataSetChanged();
                Gson gson = new Gson();
                sessionManager.setItemList(gson.toJson(allItems));
                Toast.makeText(holder.myView.getContext(), "Item Deleted Successfully", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
