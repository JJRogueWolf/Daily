package com.aswarth.dailyApp;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import static com.aswarth.dailyApp.AppController.allItems;
import static com.aswarth.dailyApp.AppController.buyItems;
import static com.aswarth.dailyApp.AppController.homeItems;

public class HomeFragment extends Fragment {

    private View view;
    private RecyclerView homeRecycler;
    TextView homeNoItemText;
    SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        homeRecycler = view.findViewById(R.id.home_recycler);
        homeNoItemText = view.findViewById(R.id.home_no_item);
        sessionManager = new SessionManager(view.getContext());
        homeRecycler.setVisibility(View.GONE);
        homeNoItemText.setVisibility(View.VISIBLE);
        setupItems();
        return view;
    }

    public void setupItems(){
        if (homeItems != null) {
            if (!homeItems.isEmpty()) {
                homeRecycler.setVisibility(View.VISIBLE);
                homeNoItemText.setVisibility(View.GONE);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
                homeRecycler.setLayoutManager(gridLayoutManager);
                ImageItemAdapter imageItemAdapter = new ImageItemAdapter(requireContext(), homeItems);
                homeRecycler.setAdapter(imageItemAdapter);
            }
        }
    }
}
