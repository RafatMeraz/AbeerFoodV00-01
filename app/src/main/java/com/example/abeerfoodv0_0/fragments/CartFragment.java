package com.example.abeerfoodv0_0.fragments;


import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.internal.NavigationMenuItemView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.adapters.CartAdapter;
import com.example.abeerfoodv0_0.adapters.FoodAdapter;
import com.example.abeerfoodv0_0.adapters.ShopAdapter;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.database.SharedPrefManager;
import com.example.abeerfoodv0_0.model.Cart;
import com.example.abeerfoodv0_0.model.Food;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.utils.Constraints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import q.rorbin.badgeview.QBadgeView;

import static com.example.abeerfoodv0_0.activities.HomeActivity.navView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    public static ArrayList<Cart> allCartList;
    private Shop currentShop;
    private TextView shopNameTV, locationTV, opeingTV, blankCartTV;
    public static TextView totalCostTV;
    private ImageView activeStatusIV, clearCartIV, shopIV;
    public static double totalPrice;
    private Button cartButton;


    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_cart, container, false);
        shopNameTV = view.findViewById(R.id.cartShopNameTV);
        locationTV = view.findViewById(R.id.cartLocationTV);
        opeingTV = view.findViewById(R.id.cartOpenHoursTV);
        activeStatusIV = view.findViewById(R.id.cartActiveStateImgView);
        clearCartIV = view.findViewById(R.id.clearCartIV);
        totalCostTV = view.findViewById(R.id.cartTotalPriceTV);
        blankCartTV = view.findViewById(R.id.blankCartTV);
        shopIV = view.findViewById(R.id.cartShopImgView);
        cartButton = view.findViewById(R.id.placeOrderButton);

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        allCartList = new ArrayList<>();

        allCartList = (ArrayList<Cart>) new DatabaseHandler(getActivity()).getAllOrders(Constraints.currentUser.getId());

        totalPrice = 0.00;
        for (Cart cart: allCartList) {
            totalPrice += (cart.getPrice() * cart.getQuantity());
        }

        totalCostTV.setText(String.valueOf(totalPrice));

        cartRecyclerView.setLayoutManager(layoutManager);
        final CartAdapter adapter = new CartAdapter(allCartList, getActivity());
        cartRecyclerView.setAdapter(adapter);

        if ( SharedPrefManager.getInstance(getActivity()).getShopID() != 0){
            blankCartTV.setVisibility(View.GONE);
            loadShopDetails(SharedPrefManager.getInstance(getActivity()).getShopID());
        } else {
            shopNameTV.setVisibility(View.GONE);
            locationTV.setVisibility(View.GONE);
            opeingTV.setVisibility(View.GONE);
            activeStatusIV.setVisibility(View.GONE);
            shopIV.setVisibility(View.GONE);
            blankCartTV.setVisibility(View.VISIBLE);
        }

        clearCartIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allCartList.size() > 0){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Carts")
                            .setMessage("Are you sure you want to delete all carts?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    new DatabaseHandler(getActivity()).deleteCarts(Constraints.currentUser.getId());
                                    shopNameTV.setVisibility(View.GONE);
                                    locationTV.setVisibility(View.GONE);
                                    opeingTV.setVisibility(View.GONE);
                                    activeStatusIV.setVisibility(View.GONE);
                                    shopIV.setVisibility(View.GONE);
                                    allCartList.clear();
                                    adapter.notifyDataSetChanged();
                                    SharedPrefManager.getInstance(getActivity()).setShopId(0);
                                    totalPrice = 0.00;
                                    totalCostTV.setText(String.valueOf(totalPrice));
                                    blankCartTV.setVisibility(View.VISIBLE);
                                    }
                            })
                            .setCancelable(false)
                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton("CANCEL", null)
                            .setIcon(getResources().getDrawable(R.drawable.ic_delete_black_24dp))
                            .show();
                }
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        return view;
    }


    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog));
        alertDialog.setTitle("One more Step");
        alertDialog.setMessage("Enter your shipping address");

        View order_address = getActivity().getLayoutInflater().inflate(R.layout.order_address_comment, null);

        final EditText addressET = order_address.findViewById(R.id.orderAddressET);
        RadioButton shipToHomeAddress = order_address.findViewById(R.id.shipToaHomeAddressRadioButton);
        RadioButton shipToGivenAddress = order_address.findViewById(R.id.shipToaGivenAddressRadioButton);
        RadioButton shipToCurrentAddress = order_address.findViewById(R.id.shipToThisAddressRadioButton);
        EditText noteET = order_address.findViewById(R.id.commentET);

        alertDialog.setView(order_address);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_blue);

        shipToHomeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressET.setText(Constraints.currentUserDetails.getAddress1());
            }
        });
        shipToCurrentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setNegativeButton("CANCEL", null);
        alertDialog.show();
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
                            locationTV.setText(currentShop.getLocation());
                            opeingTV.setText(currentShop.getOpenAt()+"-"+currentShop.getCloseAT());
                            if (currentShop.getIsOpen()==0){
                                activeStatusIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_inactive_red));
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
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

}
