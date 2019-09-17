package com.example.abeerfoodv0_0.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.database.SharedPrefManager;
import com.example.abeerfoodv0_0.model.Cart;
import com.example.abeerfoodv0_0.model.Order;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private RecyclerView cartRecyclerView;
    public static ArrayList<Cart> allCartList;
    private Shop currentShop;
    private TextView shopNameTV, locationTV, opeingTV, blankCartTV;
    public static TextView totalCostTV;
    private ImageView activeStatusIV, clearCartIV, shopIV;
    public static double totalPrice;
    public static int totalQuantity;
    private Button cartButton;
    private CartAdapter adapter;
    private CardView cardView;

    //Location Fetcher
    private Location location;
    private Double latitude, longitude;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;




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
        cardView = view.findViewById(R.id.cartShopDetailsCardView);


        //Location Fetcher
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(getActivity()).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

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
        adapter = new CartAdapter(allCartList, getActivity());
        cartRecyclerView.setAdapter(adapter);

        if ( SharedPrefManager.getInstance(getActivity()).getShopID() != 0){
            blankCartTV.setVisibility(View.GONE);
            loadShopDetails(SharedPrefManager.getInstance(getActivity()).getShopID());
        } else {
            cardView.setVisibility(View.GONE);
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
                if (allCartList.size() > 0){
                    LocationManager lm = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
                    boolean gps_enabled = false;

                    try {
                        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    } catch(Exception ex) {}

                    if(!gps_enabled) {
                        // notify user
                        new AlertDialog.Builder(getActivity())
                                .setMessage("Please enable your GPS Location")
                                .setPositiveButton("ENABLE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                })
                                .setNegativeButton("CANCEL", null)
                                .show();
                    } else {
                        showAlertDialog();
                    }
                }
            }
        });

        return view;
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                getActivity().finish();
            }

            return false;
        }

        return true;
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
        final EditText noteET = order_address.findViewById(R.id.commentET);

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
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    addressET.setText(address);

                } catch (IOException e) {
                    DynamicToast.makeError(getActivity(), "Couldn't Fetch your current location. Please turn on your location or internet!", Toast.LENGTH_LONG).show();
                }
}
        });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item_list = "";
                for (Cart cart:allCartList)
                    totalQuantity += cart.getQuantity();
                for (Cart cart : allCartList){
                    item_list+= cart.getProductName()+":"+cart.getQuantity()+" ";
                }
                Order newOrder = new Order(
                        Constraints.currentUser.getId(),
                        currentShop.getId(),
                        totalQuantity,
                        totalPrice,
                        latitude,
                        longitude,
                        item_list,
                        addressET.getText().toString(),
                        noteET.getText().toString(),
                        2,
                        "dsfdsfdsf45454545"
                        );
                if (Constraints.isConnectedToInternet(getActivity())) {
                    orderFood(newOrder);
                }
            }
        });

        alertDialog.setNegativeButton("CANCEL", null);
        alertDialog.show();
    }

    private void orderFood(final Order newOrder) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Placing order");
        dialog.setMessage("Please wait a while...");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.ORDER_FOOD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dialog.hide();
                            JSONObject msg = new JSONObject(response);
                            Log.e("response : ", response.toString());

                            if (msg.getBoolean("error"))
                                DynamicToast.makeError(getActivity(), msg.getString("message"), Toast.LENGTH_SHORT).show();
                            else{
                                DynamicToast.makeSuccess(getActivity(), msg.getString("message"), Toast.LENGTH_SHORT).show();
                                allCartList.clear();
                                adapter.notifyDataSetChanged();
                                SharedPrefManager.getInstance(getActivity()).setShopId(0);
                                cardView.setVisibility(View.GONE);
                                new DatabaseHandler(getActivity()).deleteCarts(Constraints.currentUser.getId());
                                totalPrice = 0.0;
                                totalCostTV.setText(""+totalPrice);
                                blankCartTV.setVisibility(View.VISIBLE);
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
                params.put("user_id", String.valueOf(newOrder.getUserId()));
                params.put("shop_id", String.valueOf(newOrder.getShopId()));
                params.put("item_list", newOrder.getItemList());
                params.put("total_price", String.valueOf(newOrder.getPrice()));
                params.put("note", newOrder.getNote());
                params.put("total_quantity", String.valueOf(newOrder.getQuantity()));
                params.put("payment_id", String.valueOf(newOrder.getPaymentMethod()));
                params.put("transaction_id", newOrder.getTransactionID());
                params.put("latitude", String.valueOf(newOrder.getLatitude()));
                params.put("longitude", String.valueOf(newOrder.getLongitude()));
                params.put("address", newOrder.getAddress());

                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
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



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e("Location: ", latitude +" "+ longitude);
        }

        startLocationUpdates();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            DynamicToast.makeError(getActivity(), "You need to install Google Play Services to use the App properly", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            DynamicToast.makeError(getActivity(), "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e("Location: ", latitude +" "+ longitude);

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(getActivity()).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }


}
