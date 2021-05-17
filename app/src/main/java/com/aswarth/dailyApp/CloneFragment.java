package com.aswarth.dailyApp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.aswarth.dailyApp.AppController.cloneItems;

public class CloneFragment extends Fragment {

    View view;
    private RecyclerView cloneRecycler;
    TextView cloneNoItemText;
    SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_clone, container, false);
        sessionManager = new SessionManager(requireContext());
        cloneRecycler = view.findViewById(R.id.clone_recycler);
        cloneNoItemText = view.findViewById(R.id.clone_no_item);
        cloneRecycler.setVisibility(View.GONE);
        cloneNoItemText.setVisibility(View.VISIBLE);
        setUpCloneAdapter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setUpCloneAdapter(){
        if (cloneItems != null) {
            if (!cloneItems.isEmpty()) {
                cloneRecycler.setVisibility(View.VISIBLE);
                cloneNoItemText.setVisibility(View.GONE);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
                cloneRecycler.setLayoutManager(gridLayoutManager);
                CloneListAdapter imageItemAdapter = new CloneListAdapter(requireContext(), cloneItems, cloneNoItemText, cloneRecycler);
                cloneRecycler.setAdapter(imageItemAdapter);
            }
        }
    }

    public void itemViewVisible(){
        cloneRecycler.setVisibility(View.VISIBLE);
        cloneNoItemText.setVisibility(View.GONE);
    }

    public void itemViewInvisible(){
        cloneRecycler.setVisibility(View.GONE);
        cloneNoItemText.setVisibility(View.VISIBLE);
    }
}
