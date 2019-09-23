package com.example.abeerfoodv0_0.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.activities.ShopDetailsActivity;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MyViewHolder> {

    ArrayList<Shop> shopArrayList;
    Context context;

    public ShopAdapter(ArrayList<Shop> shopArrayList, Context context) {
        this.shopArrayList = shopArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_shop_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        myViewHolder.shopNameTV.setText(shopArrayList.get(i).getShopName());
        myViewHolder.locationTV.setText(shopArrayList.get(i).getLocation());
        myViewHolder.openingTV.setText(shopArrayList.get(i).getOpenAt() +"-"+ shopArrayList.get(i).getCloseAT());
        if (shopArrayList.get(i).getIsOpen() == 0)
            myViewHolder.activeStatusIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inactive_red));
        if (new DatabaseHandler(context).isFav(shopArrayList.get(i).getId(), Constraints.currentUser.getId())) {
            myViewHolder.favIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_full));
        }

        myViewHolder.favIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new DatabaseHandler(context).isFav(shopArrayList.get(i).getId(), Constraints.currentUser.getId())) {
                    new DatabaseHandler(context).removeToFavourites(shopArrayList.get(i).getId(), Constraints.currentUser.getId());
                    myViewHolder.favIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                } else {
                    new DatabaseHandler(context).addFav(shopArrayList.get(i), Constraints.currentUser.getId());
                    myViewHolder.favIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_full));
                }
            }
        });

        if (shopArrayList.get(i).getImage()!="default.png"){
            Picasso.with(context).load(Constraints.IMG_BASE_URL+shopArrayList.get(i).getImage()).into(myViewHolder.restaurantIV);
        }

        myViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopDetailsActivity.class);
                intent.putExtra("shop_id", shopArrayList.get(i).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return shopArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView shopNameTV, locationTV, openingTV;
        ImageView restaurantIV, activeStatusIV, favIV;
        ConstraintLayout constraintLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shopNameTV = itemView.findViewById(R.id.shopDetailShopNameTV);
            locationTV = itemView.findViewById(R.id.shopDetailLocationTV);
            openingTV = itemView.findViewById(R.id.shopDetailOpenHoursTV);
            activeStatusIV = itemView.findViewById(R.id.shopDetailActiveStateImgView);
            favIV = itemView.findViewById(R.id.singleShopFavouriteIV);
            restaurantIV = itemView.findViewById(R.id.restaurantImgView);
            constraintLayout = itemView.findViewById(R.id.singleShopConstraintLayout);
        }
    }

}