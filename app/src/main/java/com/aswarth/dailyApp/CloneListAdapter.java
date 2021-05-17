package com.aswarth.dailyApp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import static com.aswarth.dailyApp.AppController.cloneItems;
import static com.aswarth.dailyApp.AppController.homeItems;
import static com.aswarth.dailyApp.AppController.mainActivityActions;
import static com.aswarth.dailyApp.AppController.storeInDB;

public class CloneListAdapter extends RecyclerView.Adapter<CloneListHolder> {
    private final ArrayList<Items> items;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    long lastClickTime = 0;
    SessionManager sessionManager;
    TextView noItemView;
    RecyclerView cloneRecycler;

    public CloneListAdapter(Context context, ArrayList<Items> items, TextView noItemView, RecyclerView cloneRecycler) {
        sessionManager = new SessionManager(context);
        this.items = items;
        this.noItemView = noItemView;
        this.cloneRecycler = cloneRecycler;
    }

    @NonNull
    @Override
    public CloneListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new CloneListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CloneListHolder holder, final int position) {
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
                    AppController.removeFromDB(items.get(position).getImageUrl(), AppController.cloned_user_id);
                    cloneItems.remove(position);
                    if (cloneItems.isEmpty()){
                        cloneRecycler.setVisibility(View.GONE);
                        noItemView.setVisibility(View.VISIBLE);
                    } else {
                        cloneRecycler.setVisibility(View.VISIBLE);
                        noItemView.setVisibility(View.GONE);
                    }
                    Gson gson = new Gson();
                    sessionManager.setCloneList(gson.toJson(cloneItems));
                    lastClickTime = 0;
                    notifyDataSetChanged();
                    Toast.makeText(holder.myView.getContext(), "Item removed from Clone", Toast.LENGTH_SHORT).show();
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
