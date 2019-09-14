package com.example.abeerfoodv0_0.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.activities.NetConnectionFailedActivity;
import com.example.abeerfoodv0_0.activities.ShopDetailsActivity;
import com.example.abeerfoodv0_0.adapters.FavouriteAdapter;
import com.example.abeerfoodv0_0.adapters.ShopAdapter;
import com.example.abeerfoodv0_0.interfaces.RecyclerItemClickListener;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private MaterialSearchBar searchBar;
    private ArrayList<Shop> shopList;
    private ArrayList<Shop> searchShopList;
    private RecyclerView searchRV;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Shop> suggestionList;
    private ArrayList<Shop> resultShopList;
    private CustomSuggestionsAdapter customSuggestionsAdapter;
    private FavouriteAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        searchBar = view.findViewById(R.id.searchBar);
        searchRV = view.findViewById(R.id.searchRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.searchSwipeRefreshLayout);

//        if (Constraints.isConnectedToInternet(getActivity())){
//            getActivity().finish();
//            startActivity(new Intent(getActivity(), NetConnectionFailedActivity.class));
//        }


        shopList = new ArrayList<>();
        suggestionList = new ArrayList<>();
        resultShopList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRV.setLayoutManager(layoutManager);
        loadShopList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadShopList();
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );


        LayoutInflater suggestionInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        customSuggestionsAdapter = new CustomSuggestionsAdapter(suggestionInflater);
        customSuggestionsAdapter.setSuggestions(suggestionList);

        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);

        searchRV.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), searchRV ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        Intent intent = new Intent(getActivity(), ShopDetailsActivity.class);
                        intent.putExtra("shop_id", shopList.get(position).getId());
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                suggestions(searchBar.getText().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                Toast.makeText(getActivity(), "erer", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    searchRV.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                loadSearchResult(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });

        return view;
    }

    private void loadSearchResult(CharSequence text) {
        resultShopList.clear();
        for (Shop shop : shopList){
            if (shop.getShopName().contains(text)){
                resultShopList.add(shop);
            }
        }

        FavouriteAdapter searchAdapter = new FavouriteAdapter(resultShopList, getActivity());
        searchRV.setAdapter(searchAdapter);
        searchRV.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), searchRV ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        Intent intent = new Intent(getActivity(), ShopDetailsActivity.class);
                        intent.putExtra("shop_id", resultShopList.get(position).getId());
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }


    private void suggestions(String name){
        suggestionList.clear();
        for (Shop shop : shopList){
            if (shop.getShopName().toLowerCase().contains(name)){
                suggestionList.add(shop);
            }
        }
        customSuggestionsAdapter.setSuggestions(suggestionList);
    }

    private void loadShopList() {
        swipeRefreshLayout.setRefreshing(true);
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
                            suggestionList.addAll(shopList);
                            //creating adapter object and setting it to recyclerview
                            adapter = new FavouriteAdapter(shopList, getActivity());
                            searchRV.setAdapter(adapter);
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
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    class CustomSuggestionsAdapter extends SuggestionsAdapter<Shop, CustomSuggestionsAdapter.SuggestionHolder> {

        public CustomSuggestionsAdapter(LayoutInflater inflater) {
            super(inflater);
        }

        class SuggestionHolder extends RecyclerView.ViewHolder{
            protected TextView title;
            protected TextView subtitle;
            protected CardView rootView;

            public SuggestionHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.suggestionTitleTV);
                subtitle = (TextView) itemView.findViewById(R.id.suggestionSubTitleTV);
                rootView = itemView.findViewById(R.id.singleSuggestionCardView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ShopDetailsActivity.class);
                        intent.putExtra("shop_id", suggestionList.get(getAdapterPosition()).getId());
                        startActivity(intent);                    }
                });
            }
        }

        @Override
        public void onBindSuggestionHolder(Shop suggestion, SuggestionHolder holder, final int position) {
            holder.title.setText(suggestion.getShopName());
            holder.subtitle.setText("The price is " + suggestion.getLocation());

        }

        @Override
        public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_custom_suggestion, parent, false);
            return new SuggestionHolder(view);        }


        @Override
        public int getSingleViewHeight() {
            return 60;
        }

    }



}


