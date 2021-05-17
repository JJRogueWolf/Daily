package com.aswarth.dailyApp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProductListView extends ArrayAdapter<Product> {
    Context context;
    Product[] products;

    public ProductListView(@NonNull Context context, @NonNull Product[] products) {
        super(context, R.layout.item_list, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
