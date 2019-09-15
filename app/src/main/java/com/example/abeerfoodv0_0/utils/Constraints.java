package com.example.abeerfoodv0_0.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abeerfoodv0_0.adapters.NewFoodGridAdapter;
import com.example.abeerfoodv0_0.model.Category;
import com.example.abeerfoodv0_0.model.Shop;
import com.example.abeerfoodv0_0.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constraints {
    public static final String IMG_BASE_URL = "http://192.168.0.104/Android/images/";
    private static final String BASE_URL = "http://192.168.0.104/Android/v1/";
    public static final String USER_REG_URL = BASE_URL +"registerUser.php";
    public static final String USER_LOGIN_URL = BASE_URL +"userLogin.php";
    public static final String SHOPS_URL = BASE_URL +"shopInfo.php";
    public static final String NEW_SHOPS_URL = BASE_URL +"newShops.php";
    public static final String SLIDERS_URL = BASE_URL +"sliders.php";
    public static final String FOODS_URL = BASE_URL +"allFoodsOfShop.php";
    public static final String SHOP_DETAILS_URL = BASE_URL +"shopDetails.php";
    public static final String USER_DETAILS_URL = BASE_URL +"userDetails.php";
    public static final String UPDATE_USER_URL = BASE_URL +"updateUser.php";
    public static final String CATEGORY_URL = BASE_URL +"foodCategory.php";
    public static final String ORDERS_URL = BASE_URL +"allOrderOfUser.php";
    public static final String ORDER_FOOD_URL = BASE_URL +"insertOrder.php";
    public static final String UPLOAD_PROFILE_IMAGE_URL = BASE_URL +"uploadProfileImg.php";

    public static User currentUser;
    public static User currentUserDetails;

    public static ArrayList<Category> categoryArrayList ;
    public static Map<Integer, String> categoryMapList;


    public static void getCategory(Context context){
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

                                categoryArrayList = new ArrayList<>();
                                categoryMapList = new HashMap<>();

                                //adding the shop to shop list
                                categoryArrayList.add(new Category(
                                        shop.getInt("id"),
                                        shop.getString("name"),
                                        shop.getString("image")
                                ));
                                categoryMapList.put(shop.getInt("id"), shop.getString("name"));
                            }
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
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){
                for (int i=0; i<info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}

