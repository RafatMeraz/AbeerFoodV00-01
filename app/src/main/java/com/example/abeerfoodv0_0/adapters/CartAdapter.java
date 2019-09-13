package com.example.abeerfoodv0_0.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.fragments.CartFragment;
import com.example.abeerfoodv0_0.model.Cart;
import com.example.abeerfoodv0_0.utils.Constraints;

import java.util.ArrayList;

import static com.example.abeerfoodv0_0.fragments.CartFragment.allCartList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    ArrayList<Cart> cartArrayList;
    Context context;

    public CartAdapter(ArrayList<Cart> cartArrayList, Context context) {
        this.cartArrayList = cartArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_cart_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.foodNameTV.setText(cartArrayList.get(i).getProductName());
        myViewHolder.priceTV.setText("$ "+cartArrayList.get(i).getPrice());
        myViewHolder.quantityButton.setNumber(String.valueOf(cartArrayList.get(i).getQuantity()));
        myViewHolder.quantityButton.setRange(1, 10);
        myViewHolder.quantityButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                allCartList.get(i).setQuantity(newValue);
                Cart newCart = allCartList.get(i);
                new DatabaseHandler(context).removeCart(Constraints.currentUser.getId(), allCartList.get(i).getId());
                new DatabaseHandler(context).addCart(newCart, Constraints.currentUser.getId());
                CartFragment.totalPrice = 0;
                for (Cart cart: allCartList) {
                    CartFragment.totalPrice += (cart.getPrice() * cart.getQuantity());
                }
                CartFragment.totalCostTV.setText(String.valueOf(CartFragment.totalPrice));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView foodNameTV, priceTV;
        ElegantNumberButton quantityButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTV = itemView.findViewById(R.id.cartLayoutFoodNameTV);
            priceTV = itemView.findViewById(R.id.cartLAyoutFoodPriceTV);
            quantityButton = itemView.findViewById(R.id.cartQuantityButton);
        }
    }

}