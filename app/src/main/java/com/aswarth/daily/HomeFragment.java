package com.aswarth.daily;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.aswarth.daily.AppController.allItems;

public class HomeFragment extends Fragment {

    private View view;
    private RecyclerView homeRecycler;
    TextView homeNoItemText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        homeRecycler = view.findViewById(R.id.home_recycler);
        homeNoItemText = view.findViewById(R.id.home_no_item);

        homeRecycler.setVisibility(View.GONE);
        homeNoItemText.setVisibility(View.VISIBLE);

        if (allItems != null) {
            if (!allItems.isEmpty()) {
                homeRecycler.setVisibility(View.VISIBLE);
                homeNoItemText.setVisibility(View.GONE);

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
                homeRecycler.setLayoutManager(gridLayoutManager);
                ImageListAdapter imageListAdapter = new ImageListAdapter(allItems);
                homeRecycler.setAdapter(imageListAdapter);
            }
        }
        return view;
    }
}
