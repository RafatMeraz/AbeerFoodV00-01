package com.example.abeerfoodv0_0.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.activities.NetConnectionFailedActivity;
import com.example.abeerfoodv0_0.activities.ShopDetailsActivity;
import com.example.abeerfoodv0_0.adapters.FavouriteAdapter;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.database.SharedPrefManager;
import com.example.abeerfoodv0_0.interfaces.RecyclerItemClickListener;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.utils.Constraints;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {


    private RecyclerView recyclerView;
    private ImageView clearFavouriteIV;
    private ArrayList<Shop> shopList;
    private TextView blankTV;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favourite, container, false);
        recyclerView = view.findViewById(R.id.favouriteRecyclerView);
        clearFavouriteIV = view.findViewById(R.id.clearFavsIV);
        blankTV = view.findViewById(R.id.blankFavsTV);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if (Constraints.isConnectedToInternet(getActivity())){
            shopList = (ArrayList<Shop>) new DatabaseHandler(getActivity()).getAllFavs(Constraints.currentUser.getId());
        }else{
            getActivity().finish();
            startActivity(new Intent(getActivity(), NetConnectionFailedActivity.class));
        }


        if (shopList.size() <= 0)
            blankTV.setVisibility(View.VISIBLE);

        final FavouriteAdapter adapter = new FavouriteAdapter(shopList, getActivity());
        recyclerView.setAdapter(adapter);
        clearFavouriteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Carts")
                        .setMessage("Are you sure you want to delete all favourites?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                new DatabaseHandler(getActivity()).deleteFavs(Constraints.currentUser.getId());
                                shopList.clear();
                                adapter.notifyDataSetChanged();
                                blankTV.setVisibility(View.VISIBLE);
                            }
                        })
                        .setCancelable(false)
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("CANCEL", null)
                        .setIcon(getResources().getDrawable(R.drawable.ic_delete_black_24dp))
                        .show();
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
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

        return view;
    }

}
