package com.example.abeerfoodv0_0.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.activities.HomeActivity;
import com.example.abeerfoodv0_0.activities.NetConnectionFailedActivity;
import com.example.abeerfoodv0_0.adapters.OrderAdapter;
import com.example.abeerfoodv0_0.adapters.ShopAdapter;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.model.Order;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.model.Slider;
import com.example.abeerfoodv0_0.model.User;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.example.abeerfoodv0_0.utils.MySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {


    private RecyclerView recyclerView;
    private ArrayList<com.example.abeerfoodv0_0.model.Request> orderList;
    private SwipeRefreshLayout swipeRefreshLayout;

    public OrderFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_order, container, false);
        recyclerView = rootView.findViewById(R.id.orderHistoryRecyclerView);
        swipeRefreshLayout = rootView.findViewById(R.id.orderSwipeRefreshLayout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if (Constraints.isConnectedToInternet(getActivity())){
            loadAllOrder(Constraints.currentUser.getId());
        } else {
            startActivity(new Intent(getActivity(), NetConnectionFailedActivity.class));
            getActivity().finish();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Constraints.isConnectedToInternet(getActivity())) {
                    swipeRefreshLayout.setRefreshing(true);
                    loadAllOrder(Constraints.currentUser.getId());
                } else {
                    startActivity(new Intent(getActivity(), NetConnectionFailedActivity.class));
                    getActivity().finish();
                }
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        return rootView;
    }


    private void loadAllOrder(final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.ORDERS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            orderList = new ArrayList<>();
                            JSONArray array = new JSONArray(response);
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject order = array.getJSONObject(i);
                                com.example.abeerfoodv0_0.model.Request request = new com.example.abeerfoodv0_0.model.Request(
                                        order.getInt("id"),
                                        order.getInt("shop_id"),
                                        order.getInt("total_quantity"),
                                        order.getInt("status"),
                                        order.getDouble("total_price"),
                                        order.getString("item_list"),
                                        order.getString("shop_name")
                                );
                                orderList.add(request);
                            }

                            Collections.reverse(orderList);
                            OrderAdapter adapter = new OrderAdapter(orderList, getActivity());
                            recyclerView.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);

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
                params.put("user_id", String.valueOf(id));
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

