package com.example.abeerfoodv0_0.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.database.SharedPrefManager;
import com.example.abeerfoodv0_0.model.Food;
import com.example.abeerfoodv0_0.model.Cart;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class FoodDetailsActivity extends AppCompatActivity {

    private TextView foodNameTV, shopNameTV, descriptionTV, priceTV, availableTV, quantityTV;
    private FloatingActionButton addButton, negButton, cartButton;
    private Food currentFood;
    private String currentShopName;
    private int currentShopID;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        currentFood = (Food) getIntent().getSerializableExtra("food");
        currentShopName = getIntent().getStringExtra("shopname");
        currentShopID = getIntent().getIntExtra("shop_id", 0);
        initialization();
    }

    private void initialization() {
        foodNameTV = findViewById(R.id.foodDetailsFoodNameTV);
        shopNameTV = findViewById(R.id.foodDetailsShopTitleTV);
        descriptionTV = findViewById(R.id.foodDetailsFoodDescriptionTV);
        priceTV = findViewById(R.id.foodDetailsPriceTV);
        backButton = findViewById(R.id.foodDetailsBackButton);
        availableTV = findViewById(R.id.foodDetailsAvailableTV);
        quantityTV = findViewById(R.id.foodDetailsFoodQuantityTV);
        addButton = findViewById(R.id.foodDetailsPosOneButton);
        negButton = findViewById(R.id.foodDetailsNegOneButton);
        cartButton = findViewById(R.id.foodDetailsCartButton);

        shopNameTV.setText(currentShopName);
        foodNameTV.setText(currentFood.getTitle());
        descriptionTV.setText(currentFood.getAbout());
        if (currentFood.getAvailable() == 0) {
            availableTV.setText("Unavailable");
            availableTV.setTextColor(Color.parseColor("#ff0000"));
        }
        priceTV.setText("$ " + currentFood.getPrice());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantityTV.getText().toString());
                if (quantity < 10)
                    quantity++;
                quantityTV.setText(String.valueOf(quantity));
            }
        });
        negButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantityTV.getText().toString());
                if (quantity > 1)
                    quantity--;
                quantityTV.setText(String.valueOf(quantity));
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefManager.getInstance(getApplicationContext()).getShopID() == 0 ||
                        SharedPrefManager.getInstance(getApplicationContext()).getShopID() == currentShopID) {
                    new DatabaseHandler(getApplicationContext()).addCart(new Cart(
                            currentFood.getTitle(),
                            Integer.parseInt(quantityTV.getText().toString()),
                            currentFood.getId(),
                            currentFood.getPrice()
                    ), Constraints.currentUser.getId());
                    SharedPrefManager.getInstance(getApplicationContext()).setShopId(currentShopID);
                    DynamicToast.makeSuccess(getApplicationContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPrefManager.getInstance(getApplicationContext()).clearShopId();
                    new DatabaseHandler(getApplicationContext()).deleteCarts(Constraints.currentUser.getId());
                    new DatabaseHandler(getApplicationContext()).addCart(new Cart(
                            currentFood.getTitle(),
                            Integer.parseInt(quantityTV.getText().toString()),
                            currentFood.getId(),
                            currentFood.getPrice()
                    ), Constraints.currentUser.getId());
                    SharedPrefManager.getInstance(getApplicationContext()).setShopId(currentShopID);
                    DynamicToast.makeSuccess(getApplicationContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
