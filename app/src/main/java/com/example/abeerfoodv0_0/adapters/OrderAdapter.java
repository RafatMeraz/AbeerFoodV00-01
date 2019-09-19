package com.example.abeerfoodv0_0.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.activities.MapActivity;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.fragments.CartFragment;
import com.example.abeerfoodv0_0.model.Cart;
import com.example.abeerfoodv0_0.model.Order;
import com.example.abeerfoodv0_0.model.Request;
import com.example.abeerfoodv0_0.utils.Constraints;

import java.util.ArrayList;

import static com.example.abeerfoodv0_0.fragments.CartFragment.allCartList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    ArrayList<Request> cartArrayList;
    Context context;

    public OrderAdapter(ArrayList<Request> cartArrayList, Context context) {
        this.cartArrayList = cartArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_order_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.shopNameTV.setText(String.valueOf(cartArrayList.get(i).getShopName()));
        myViewHolder.priceTV.setText("$ "+cartArrayList.get(i).getPrice());
        myViewHolder.itemTV.setText(cartArrayList.get(i).getItemList());
        String status = null;
        String[] colors = {"#4caf50", "#cddc39", "#ff9800", "#ff5722"};
        String color = null;
        switch (cartArrayList.get(i).getStatus()){
            case 0:
                color = colors[0];
                status = "Placed";
                break;
            case 1:
                color = colors[1];
                status = "Preparing";
                break;
            case 2:
                color = colors[2];
                status = "Shipping";
                break;
            case 3:
                color = colors[3];
                status = "Shipped";
                break;
        }
        myViewHolder.statusTV.setTextColor(Color.parseColor(color));
        myViewHolder.statusTV.setText(status);
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (cartArrayList.get(i).getStatus()==2){
//
//                }
                context.startActivity(new Intent(context, MapActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView shopNameTV, priceTV, itemTV, statusTV;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shopNameTV = itemView.findViewById(R.id.singleOrderShopNameTV);
            priceTV = itemView.findViewById(R.id.singleOrderTotalPriceTV);
            itemTV = itemView.findViewById(R.id.singleOrderItemsTV);
            statusTV = itemView.findViewById(R.id.singleOrderStatusTV);
            cardView = itemView.findViewById(R.id.singleOrderCardView);
        }
    }

}