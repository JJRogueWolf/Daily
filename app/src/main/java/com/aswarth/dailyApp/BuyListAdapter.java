package com.aswarth.dailyApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import static com.aswarth.dailyApp.AppController.allItems;
import static com.aswarth.dailyApp.AppController.buyItems;
import static com.aswarth.dailyApp.AppController.buyListCount;
import static com.aswarth.dailyApp.AppController.homeItems;

public class BuyListAdapter extends RecyclerView.Adapter<BuyListHolder> {
    private final ArrayList<Items> items;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    long lastClickTime = 0;
    SessionManager sessionManager;

    public BuyListAdapter(Context context, ArrayList<Items> items) {
        sessionManager = new SessionManager(context);
        this.items = items;
    }

    @NonNull
    @Override
    public BuyListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new BuyListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final BuyListHolder holder, final int position) {
//        holder.mProductImage.setImageURI(items.get(position).getImageUri());
        if (items.get(position).getImageUrl().isEmpty()) {
            holder.mProductImage.setImageURI(items.get(position).getImageUri());
        } else {
            Glide.with(holder.myView.getContext()).load(items.get(position).getImageUrl()).placeholder(holder.myView.getContext().getResources().getDrawable(R.drawable.placeholder)).into(holder.mProductImage);
        }
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    AppController.removeFromDB(items.get(position).getImageUrl(), AppController.user_id);
                    homeItems.add(items.get(position));
                    buyItems.remove(items.get(position));
                    Gson gson = new Gson();
                    sessionManager.setBuyItemList(gson.toJson(buyItems));
                    lastClickTime = 0;
                    notifyDataSetChanged();
                    Toast.makeText(holder.myView.getContext(), "Item removed from Shop", Toast.LENGTH_SHORT).show();
                    if(buyItems.size() == 0){
                        buyListCount.setVisibility(View.GONE);
                    } else {
                        buyListCount.setVisibility(View.VISIBLE);
                        buyListCount.setText(String.valueOf(buyItems.size()));
                    }
                    return;
                }
                lastClickTime = clickTime;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
