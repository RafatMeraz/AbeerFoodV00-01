package com.example.abeerfoodv0_0.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.abeerfoodv0_0.model.Cart;
import com.example.abeerfoodv0_0.model.Shop;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ordermanager";
    private static final String TABLE_CARTS = "carts";
    private static final String TABLE_FAVS = "fav_list";
    private static final String KEY_ID = "product_id";
    private static final String FOOD_ID = "food_id";
    private static final String KEY_NAME = "product_name";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_PRICE = "price";
    private static final String KEY_IMG = "img";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_SHOP_ID = "shop_id";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_SHOP_NAME = "shop_name";
    private static final String KEY_SHOP_OPEN= "open_at";
    private static final String KEY_SHOP_CLOSE= "close_at";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CARTS_TABLE = "CREATE TABLE " + TABLE_CARTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_QUANTITY + " int," + KEY_PRICE + " DOUBLE(8, 2),"
                + KEY_USER_ID+ " int)";
        String CREATE_FAVS_TABLE = "CREATE TABLE " + TABLE_FAVS + "("
                + KEY_SHOP_ID + " int, "+ KEY_SHOP_NAME+ " TEXT, "
                + KEY_LOCATION + " TEXT, "+ KEY_IMG+ " TEXT, "
                + KEY_SHOP_OPEN+" TEXT, "+ KEY_SHOP_CLOSE+" TEXT, "
                + KEY_USER_ID + " int, PRIMARY KEY(shop_id, id))";
        db.execSQL(CREATE_CARTS_TABLE);
        db.execSQL(CREATE_FAVS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARTS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new cart
    public void addCart(Cart cart, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, id);
        values.put(KEY_ID, cart.getId());
        values.put(KEY_NAME, cart.getProductName());
        values.put(KEY_PRICE, cart.getPrice());
        values.put(KEY_QUANTITY, cart.getQuantity());
        // Inserting Row
        db.insert(TABLE_CARTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all carts in a list view
    public List<Cart> getAllOrders(int id) {
        List<Cart> contactList = new ArrayList<Cart>();
        // Select All Query
        String selectQuery = String.format("SELECT  * FROM " + TABLE_CARTS+ " WHERE id='%d'", id);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Cart cart = new Cart();
                cart.setId(cursor.getInt(0));
                cart.setProductName(cursor.getString(1));
                cart.setQuantity(cursor.getInt(2));
                cart.setPrice((float) cursor.getDouble(3));
                // Adding orders to list
                contactList.add(cart);
            } while (cursor.moveToNext());
        }

        // return orders list
        return contactList;
    }

    public List<Shop> getAllFavs(int id) {
        List<Shop> shopArrayList = new ArrayList<Shop>();
        // Select All Query
        String selectQuery = String.format("SELECT  * FROM " + TABLE_FAVS+ " WHERE id='%d'", id);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Shop shop = new Shop();
                shop.setId(cursor.getInt(0));
                shop.setShopName(cursor.getString(1));
                shop.setLocation(cursor.getString(2));
                shop.setImage(cursor.getString(3));
                shop.setOpenAt(cursor.getString(4));
                shop.setCloseAT(cursor.getString(5));
                // Adding orders to list
                shopArrayList.add(shop);
            } while (cursor.moveToNext());
        }

        // return orders list
        return shopArrayList;
    }

    public void addFav(Shop shop, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, id);
        values.put(KEY_SHOP_NAME, shop.getShopName());
        values.put(KEY_SHOP_ID, shop.getId());
        values.put(KEY_LOCATION, shop.getLocation());
        values.put(KEY_IMG, shop.getImage());
        values.put(KEY_SHOP_OPEN, shop.getOpenAt());
        values.put(KEY_SHOP_CLOSE, shop.getCloseAT());
        // Inserting Row
        db.insert(TABLE_FAVS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public void removeToFavourites(int shopId, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("DELETE FROM fav_list WHERE shop_id='%d' and id='%s'", shopId, id);
        db.execSQL(query);
    }

    public void deleteCarts(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM "+TABLE_CARTS + " WHERE id='%d'", id));
    }
    public void deleteFavs(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM "+TABLE_FAVS + " WHERE id='%d'", id));
    }
    public void removeCart(int id, int foodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM "+TABLE_CARTS + " WHERE id='%d' and product_id='%d'", id, foodId));
    }

    public boolean isFav(int shopId, int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("SELECT * FROM fav_list WHERE shop_id='%d' and id='%d'", shopId, id);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int getCountCart(int id) {
        int count = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("SELECT COUNT(*) FROM carts WHERE id='%d'", id);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                count = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return count;
    }

    public int getCountFavs(int id) {
        int count = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("SELECT COUNT(*) FROM fav_list WHERE id='%d'", id);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                count = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Cart cart) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("UPDATE carts SET quantity=%d WHERE product_id=%d", cart.getQuantity(), cart.getId());
        db.execSQL(query);
    }
}
