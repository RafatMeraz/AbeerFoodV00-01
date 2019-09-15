package com.example.abeerfoodv0_0.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.activities.NetConnectionFailedActivity;
import com.example.abeerfoodv0_0.adapters.OrderAdapter;
import com.example.abeerfoodv0_0.adapters.ShopAdapter;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.model.Order;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.utils.Constraints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {


    private RecyclerView recyclerView;
    private ArrayList<Order> orderList;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_order, container, false);
        recyclerView = rootView.findViewById(R.id.orderHistoryRecyclerView);

        orderList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if (Constraints.isConnectedToInternet(getActivity())){
            loadAllOrder(Constraints.currentUser.getId());
        } else {
            startActivity(new Intent(getActivity(), NetConnectionFailedActivity.class));
            getActivity().finish();
        }
        return rootView;
    }


    private void loadAllOrder(final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.ORDERS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray shop = new JSONArray(response);
                            for (int i=0; i<shop.length(); i++){
                                JSONObject order = shop.getJSONObject(i);
                                orderList.add(new Order(
                                        order.getInt("id"),
                                        order.getInt("user_id"),
                                        order.getInt("shop_id"),
                                        order.getInt("total_quantity"),
                                        order.getInt("driver_id"),
                                        order.getInt("status"),
                                        order.getDouble("total_price"),
                                        order.getDouble("latitude"),
                                        order.getDouble("longitude"),
                                        order.getString("item_list"),
                                        order.getString("address"),
                                        order.getString("note"),
                                        order.getInt("payment_id"),
                                        order.getString("transaction_id")
                                ));
                            }

                            OrderAdapter adapter = new OrderAdapter(orderList, getActivity());
                            recyclerView.setAdapter(adapter);

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
                params.put("user_id", String.valueOf(id));
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }


}
