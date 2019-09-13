package com.example.abeerfoodv0_0.database;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mContext;

    private static final String SHARED_PREF_NAME = "abeerfoodsharepref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ID = "id";
    private static final String KEY_SHOP_ID = "shop_id";


    private SharedPrefManager(Context context) {
        // Specify the application context
        mContext = context;
        // Get the request queue
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        // If Instance is null then initialize new Instance
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        // Return RequestHandler new Instance
        return mInstance;
    }

    public boolean userLogin(int id, String userName, String email){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
        return true;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_USERNAME, null) != null) {
            return true;
        }
        return false;
    }

    public int getShopID(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_SHOP_ID, 0);
    }

    public boolean setShopId(int shopId){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SHOP_ID, shopId);
        editor.apply();
        return true;
    }
    public boolean clearShopId(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SHOP_ID, 0);
        editor.apply();
        return true;
    }

    public boolean logout(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getUserName(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    public int getUserID(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, 0);

    }
    public String getUserEmail(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null);
    }
}