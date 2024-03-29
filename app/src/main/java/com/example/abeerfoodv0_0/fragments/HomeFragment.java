package com.example.abeerfoodv0_0.fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.activities.HomeActivity;
import com.example.abeerfoodv0_0.activities.NetConnectionFailedActivity;
import com.example.abeerfoodv0_0.activities.ProfileActivity;
import com.example.abeerfoodv0_0.activities.ShopDetailsActivity;
import com.example.abeerfoodv0_0.adapters.NewFoodGridAdapter;
import com.example.abeerfoodv0_0.adapters.ShopAdapter;
import com.example.abeerfoodv0_0.database.SharedPrefManager;
import com.example.abeerfoodv0_0.interfaces.RecyclerItemClickListener;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.model.Slider;
import com.example.abeerfoodv0_0.model.User;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener{


    private RecyclerView  allShopsRecyclerView;
    private GridView newShopRecyclerView;
    private ArrayList<Shop> shopList;
    private ArrayList<Shop> newShopList;
    private ArrayList<Slider> sliderList;
    private TextView userNameTV, filterTV;
    private SliderLayout sliderLayout;
    private CircleImageView homeProfileIV;
    private SwipeRefreshLayout homeSwipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        newShopRecyclerView = view.findViewById(R.id.homeNewShopRecyclerView);
        allShopsRecyclerView = view.findViewById(R.id.homeRestaurantRecyclerView);
        userNameTV = view.findViewById(R.id.homeProfileName);
        filterTV = view.findViewById(R.id.homeFilterButton);
        sliderLayout = view.findViewById(R.id.homeSliderLayout);
        homeProfileIV = view.findViewById(R.id.homeProfileIV);
        homeSwipeRefreshLayout = view.findViewById(R.id.homeFragmentSwipeRefreshLayout);


        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        shopList = new ArrayList<>();
        sliderList = new ArrayList<>();

        String userName = "<u>"+ Constraints.currentUser.getName() +"</u>";
        userNameTV.setText(Html.fromHtml(userName));

        LinearLayoutManager linearLayout2 = new LinearLayoutManager(getActivity());
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        newShopList = new ArrayList<>();

        allShopsRecyclerView.setLayoutManager(linearLayout);
        allShopsRecyclerView.setNestedScrollingEnabled(false);

        if (Constraints.isConnectedToInternet(getActivity())){
            homeSwipeRefreshLayout.setRefreshing(true);
            loadSliders();
            loadShopList();
            loadNewShopList();
        } else {
            startActivity(new Intent(getActivity(), NetConnectionFailedActivity.class));
            getActivity().finish();
        }


        homeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Constraints.isConnectedToInternet(getActivity())) {
                    homeSwipeRefreshLayout.setRefreshing(true);
                    loadShopList();
                    loadSliders();
                    loadNewShopList();
                } else {
                    startActivity(new Intent(getActivity(), NetConnectionFailedActivity.class));
                    getActivity().finish();
                }
            }
        });

        homeSwipeRefreshLayout.setColorSchemeColors(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        userNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("id", Constraints.currentUser.getId());
                startActivity(intent);
            }
        });

        homeProfileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("id", Constraints.currentUser.getId());
                startActivity(intent);
            }
        });

        loadUserDetails(Constraints.currentUser.getId());
        return view;
    }

    private void loadShopList() {
        shopList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constraints.SHOPS_URL,
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
                                shopList.add(new Shop(
                                        shop.getInt("id"),
                                        shop.getInt("is_open"),
                                        shop.getString("shop_name"),
                                        shop.getString("slug"),
                                        shop.getString("phone_number"),
                                        shop.getString("open_at"),
                                        shop.getString("close_at"),
                                        shop.getString("location"),
                                        shop.getString("image")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            ShopAdapter adapter = new ShopAdapter(shopList, getActivity());
                            allShopsRecyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadNewShopList() {

        newShopList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constraints.NEW_SHOPS_URL,
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
                                newShopList.add(new Shop(
                                        shop.getInt("id"),
                                        shop.getInt("is_open"),
                                        shop.getString("shop_name"),
                                        shop.getString("slug"),
                                        shop.getString("phone_number"),
                                        shop.getString("open_at"),
                                        shop.getString("close_at"),
                                        shop.getString("location"),
                                        shop.getString("image")
                                ));
                            }
                            int noofcoloum=newShopList.size();

                            int totalWidth =(500*noofcoloum)*2;

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(totalWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                            newShopRecyclerView.setLayoutParams(params);
                            newShopRecyclerView.setNumColumns(noofcoloum);
                            NewFoodGridAdapter adapter = new NewFoodGridAdapter(getActivity(), newShopList);
                            newShopRecyclerView.setAdapter(adapter);
                            newShopRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Toast.makeText(getContext(),position+"dsfdsf",Toast.LENGTH_LONG).show();
                                }

                            });

                            homeSwipeRefreshLayout.setRefreshing(false);

                            //creating adapter object and setting it to recyclerview
//                            newShopRecyclerView.setNumColumns(newShopList.size());
//                            NewFoodGridAdapter adapter = new NewFoodGridAdapter(getActivity(), newShopList);
//                            newShopRecyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadSliders() {

        sliderList.clear();
        sliderLayout.removeAllSliders();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constraints.SLIDERS_URL,
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
                                sliderList.add(new Slider(
                                        shop.getInt("id"),
                                        shop.getString("image")
                                ));
                            }

                            for(Slider slider : sliderList){
                                TextSliderView textSliderView = new TextSliderView(getActivity());
                                // initialize a SliderLayout
                                textSliderView
                                        .image(Constraints.IMG_BASE_URL+slider.getImage())
                                        .setScaleType(BaseSliderView.ScaleType.Fit);

                                sliderLayout.addSlider(textSliderView);
                            }
                            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
                            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                            sliderLayout.setCustomAnimation(new DescriptionAnimation());
                            sliderLayout.setDuration(5000);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadUserDetails(final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.USER_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject userResponse = new JSONObject(response);
                            Log.e("response : ", response.toString());

                            User user = new User(
                                    id,
                                    userResponse.getString("email"),
                                    userResponse.getString("name"),
                                    userResponse.getString("addressl1"),
                                    userResponse.getString("addressl2"),
                                    userResponse.getString("phone_number"),
                                    userResponse.getString("image")
                            );

                            Constraints.currentUserDetails = user;

                            if (!user.getImg().equals("default.png"))
                                Picasso.with(getActivity()).load(Constraints.IMG_BASE_URL+Constraints.currentUserDetails.getImg()).into(homeProfileIV);

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
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onResume() {
        super.onResume();
        String userName = "<u>"+ Constraints.currentUser.getName() +"</u>";
        userNameTV.setText(Html.fromHtml(userName));
        sliderLayout.startAutoCycle();
    }
}
