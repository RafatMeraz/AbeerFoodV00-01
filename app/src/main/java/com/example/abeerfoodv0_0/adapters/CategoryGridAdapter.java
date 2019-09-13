package com.example.abeerfoodv0_0.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.model.Category;

import java.util.ArrayList;

public class CategoryGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Category> categoryArrayList;

    // Constructor
    public CategoryGridAdapter(Context c, ArrayList<Category> categoryArrayList){
        this.mContext = c;
        this.categoryArrayList = categoryArrayList;

    }

    @Override
    public int getCount() {
        return categoryArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_category_layout, parent, false);
        TextView categoryNameTV = view.findViewById(R.id.singleCategoryNameTV);
        ImageView categoryIV = view.findViewById(R.id.singleCategoryImgIV);

        categoryNameTV.setText(categoryArrayList.get(position).getCategoryName());

        return view;
    }

}
