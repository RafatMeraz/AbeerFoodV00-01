package com.example.abeerfoodv0_0.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.adapters.FoodAdapter;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.interfaces.RecyclerItemClickListener;
import com.example.abeerfoodv0_0.model.Food;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView favIV, shopCoverIV, isActive;
    private TextView shopNameTV, locataionTV, openHourTV, categoryTV, moreInfoTV;
    private int currentShopID;
    private RecyclerView foodRecyclerView;
    private ImageButton backButton;
    private ArrayList<Food> foodList;
    private Shop currentShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        currentShopID =  getIntent().getIntExtra("shop_id", 0);

        initialization();

    }

    private void initialization() {
        favIV = findViewById(R.id.shopDetailsFavouriteIV);
        shopCoverIV = findViewById(R.id.shopDetailCoverIV);
        isActive = findViewById(R.id.shopDetailActiveStateImgView);
        shopNameTV = findViewById(R.id.shopDetailShopNameTV);
        locataionTV = findViewById(R.id.shopDetailLocationTV);
        openHourTV = findViewById(R.id.shopDetailOpenHoursTV);
        moreInfoTV = findViewById(R.id.shopDetailShopDetailTV);
        foodRecyclerView = findViewById(R.id.shopDetailsFoodDetailsRecyclerview);
        backButton = findViewById(R.id.shopDetailBackButton);

//        if (!Constraints.isConnectedToInternet(this)){
//            finish();
//            startActivity(new Intent(this, NetConnectionFailedActivity.class));
//        }

        loadShopDetails(currentShopID);

        foodList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        foodRecyclerView.setLayoutManager(layoutManager);

        loadShopList(currentShopID);
        foodRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), foodRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
//                         do whatever
                        if (foodList.get(position).getAvailable()==1){
                            Intent intent = new Intent(getApplicationContext(), FoodDetailsActivity.class);
                            intent.putExtra("food", foodList.get(position));
                            intent.putExtra("shopname",currentShop.getShopName());
                            intent.putExtra("shop_id",currentShop.getId());
                            startActivity(intent);
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        favIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new DatabaseHandler(getApplicationContext()).isFav(currentShop.getId(), Constraints.currentUser.getId())) {
                    new DatabaseHandler(getApplicationContext()).removeToFavourites(currentShop.getId(), Constraints.currentUser.getId());
                    favIV.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                    DynamicToast.makeSuccess(getApplicationContext(),"Removed from favourite!", Toast.LENGTH_SHORT).show();
                } else {
                    new DatabaseHandler(getApplicationContext()).addFav(currentShop, Constraints.currentUser.getId());
                    favIV.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_favorite_full));
                    DynamicToast.makeSuccess(getApplicationContext(),"Added to favourite!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadShopList(final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.FOODS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting shop object from json array
                                JSONObject shop = array.getJSONObject(i);

                                //adding the shop to shop list
                                foodList.add(new Food(
                                        shop.getInt("id"),
                                        shop.getInt("available"),
                                        shop.getString("title"),
                                        shop.getString("about"),
                                        shop.getString("image"),
                                        shop.getDouble("price")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            FoodAdapter adapter = new FoodAdapter(foodList, getApplicationContext());
                            foodRecyclerView.setAdapter(adapter);
                            Log.e("ERROR : ", foodList.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("shop_id", String.valueOf(id));
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void loadShopDetails(final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.SHOP_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                             JSONObject shop = new JSONObject(response);
                             Log.e("response : ", response.toString());

                            currentShop = new Shop(
                                     id,
                                     shop.getInt("is_open"),
                                     shop.getString("shop_name"),
                                     shop.getString("slug"),
                                     shop.getString("phone_number"),
                                     shop.getString("open_at"),
                                     shop.getString("close_at"),
                                     shop.getString("location"),
                                     shop.getString("image")
                                     );
                            shopNameTV.setText(currentShop.getShopName());
                            locataionTV.setText(currentShop.getLocation());
                            openHourTV.setText(currentShop.getOpenAt()+"-"+currentShop.getCloseAT());
                            if (currentShop.getIsOpen()==0){
                                isActive.setImageDrawable(getResources().getDrawable(R.drawable.ic_inactive_red));
                            }
                            if (new DatabaseHandler(getApplicationContext()).isFav(currentShop.getId(), Constraints.currentUser.getId())) {
                                favIV.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_favorite_full));
                            }
                            //adding the shop to shop list
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

}
