package com.aswarth.daily;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.aswarth.daily.AppController.allItems;
import static com.aswarth.daily.AppController.buyItems;

public class BuyFragment extends Fragment {

    View view;
    RecyclerView buyRecycler;
    TextView buyNoItemText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_buy, container, false);
        buyRecycler = view.findViewById(R.id.buy_recycler);
        buyNoItemText = view.findViewById(R.id.buy_no_item);

        buyRecycler.setVisibility(View.GONE);
        buyNoItemText.setVisibility(View.VISIBLE);

        if (buyItems != null) {
            if (!buyItems.isEmpty()) {
                buyRecycler.setVisibility(View.VISIBLE);
                buyNoItemText.setVisibility(View.GONE);
                buyRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                ImageListAdapter imageListAdapter = new ImageListAdapter(buyItems);
                buyRecycler.setAdapter(imageListAdapter);
            }
        }
        return view;
    }
}
