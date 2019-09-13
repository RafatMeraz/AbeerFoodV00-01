package com.example.abeerfoodv0_0.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.activities.ShopDetailsActivity;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

public class NewFoodGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Shop> shopArrayList;

    // Constructor
    public NewFoodGridAdapter(Context c, ArrayList<Shop> shopArrayList){
        this.mContext = c;
        this.shopArrayList = shopArrayList;

    }

    @Override
    public int getCount() {
        return shopArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return shopArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_new_shop_layout, parent, false);
        TextView shopNameTV = view.findViewById(R.id.shopDetailShopNameTV);
        TextView locationTV = view.findViewById(R.id.shopDetailLocationTV);
        TextView openingTV = view.findViewById(R.id.shopDetailOpenHoursTV);
        TextView categoryTV = view.findViewById(R.id.shopDetailShopCategoryTV);
        ImageView activeStatusIV = view.findViewById(R.id.shopDetailActiveStateImgView);
        final ImageView favIV = view.findViewById(R.id.singleNewShopFavouriteIV);
        CardView cardView = view.findViewById(R.id.singleShopCardView);

        shopNameTV.setText(shopArrayList.get(position).getShopName());
        locationTV.setText(shopArrayList.get(position).getLocation());
        openingTV.setText(shopArrayList.get(position).getOpenAt()+"-"+shopArrayList.get(position).getCloseAT());
        if (shopArrayList.get(position).getIsOpen() == 0)
            activeStatusIV.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_inactive_red));
        if (new DatabaseHandler(mContext).isFav(shopArrayList.get(position).getId(), Constraints.currentUser.getId())) {
            favIV.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_full));
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShopDetailsActivity.class);
                intent.putExtra("shop_id", shopArrayList.get(position).getId());
                mContext.startActivity(intent);
            }
        });

        favIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new DatabaseHandler(mContext).isFav(shopArrayList.get(position).getId(), Constraints.currentUser.getId())) {
                    new DatabaseHandler(mContext).removeToFavourites(shopArrayList.get(position).getId(), Constraints.currentUser.getId());
                    favIV.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                    DynamicToast.makeSuccess(mContext,"Removed from favourite!", Toast.LENGTH_SHORT).show();

                } else {
                    new DatabaseHandler(mContext).addFav(shopArrayList.get(position), Constraints.currentUser.getId());
                    favIV.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_full));
                    DynamicToast.makeSuccess(mContext,"Added to favourite!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
