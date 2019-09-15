package com.example.abeerfoodv0_0.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.model.Food;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> {

    ArrayList<Food> foodArrayList;
    Context context;

    public FoodAdapter(ArrayList<Food> foodArrayList, Context context) {
        this.foodArrayList = foodArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_food_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.foodNameTV.setText(foodArrayList.get(i).getTitle());
        myViewHolder.priceTV.setText("$ "+foodArrayList.get(i).getPrice());
        myViewHolder.descriptionTV.setText(foodArrayList.get(i).getAbout());
        if (foodArrayList.get(i).getAvailable() == 0) {
            myViewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.dark_white));
            myViewHolder.availableTV.setText("Unavailable");
            myViewHolder.availableTV.setTextColor(Color.parseColor("#ff0000"));
        }
    }

    @Override
    public int getItemCount() {
        return foodArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView foodNameTV, descriptionTV, priceTV, availableTV;
        CardView rootView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTV = itemView.findViewById(R.id.foodNameTV);
            descriptionTV = itemView.findViewById(R.id.aboutFoodTV);
            priceTV = itemView.findViewById(R.id.foodPriceTV);
            availableTV = itemView.findViewById(R.id.foodAvailableTV);
            rootView = itemView.findViewById(R.id.singleFoodCardView);
        }
    }

}