package com.example.abeerfoodv0_0.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.fragments.CartFragment;
import com.example.abeerfoodv0_0.model.Cart;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.utils.Constraints;

import java.util.ArrayList;

import static com.example.abeerfoodv0_0.fragments.CartFragment.allCartList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    ArrayList<Shop> favouriteArrayList;
    Context context;

    public FavouriteAdapter(ArrayList<Shop> favouriteArrayList, Context context) {
        this.favouriteArrayList = favouriteArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_favourite_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.shopNameTV.setText(favouriteArrayList.get(i).getShopName());
        myViewHolder.locationTV.setText(favouriteArrayList.get(i).getLocation());
        myViewHolder.openingHoursTV.setText(favouriteArrayList.get(i).getOpenAt()+"-"+favouriteArrayList.get(i).getCloseAT());
    }

    @Override
    public int getItemCount() {
        return favouriteArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView shopNameTV, locationTV, openingHoursTV;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shopNameTV = itemView.findViewById(R.id.singleFavShopNameTV);
            locationTV = itemView.findViewById(R.id.singleFavShopLocationTV);
            openingHoursTV = itemView.findViewById(R.id.singleFavShopOpeningHoursTV);
            imageView = itemView.findViewById(R.id.singleFavShopImgView);
        }
    }

}