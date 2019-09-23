package com.example.abeerfoodv0_0.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.database.DatabaseHandler;
import com.example.abeerfoodv0_0.fragments.CartFragment;
import com.example.abeerfoodv0_0.fragments.FavouriteFragment;
import com.example.abeerfoodv0_0.fragments.HomeFragment;
import com.example.abeerfoodv0_0.fragments.OrderFragment;
import com.example.abeerfoodv0_0.fragments.SearchFragment;
import com.example.abeerfoodv0_0.model.User;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.example.abeerfoodv0_0.database.SharedPrefManager;

import q.rorbin.badgeview.QBadgeView;

public class HomeActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private FrameLayout frameLayout;
    public static BottomNavigationView navView;
    public static int currentFragment = R.id.navigation_home;
    private boolean doubleClickedExit = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("ResourceType")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (currentFragment != R.id.navigation_home){
                        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                        Fragment mFrag = new HomeFragment();
                        t.replace(R.id.frameLayout, mFrag);
                        t.commit();
                        currentFragment = R.id.navigation_home;
                    }
                    return true;
                case R.id.navigation_search:
                    if (currentFragment != R.id.navigation_search){
                        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                        Fragment mFrag1 = new SearchFragment();
                        t.replace(R.id.frameLayout, mFrag1);
                        t.commit();
                        currentFragment = R.id.navigation_search;
                    }
                    return true;
                case R.id.navigation_orders:
                    if (currentFragment != R.id.navigation_orders){
                        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                        Fragment mFrag2 = new OrderFragment();
                        t.replace(R.id.frameLayout, mFrag2);
                        t.commit();
                        currentFragment = R.id.navigation_orders;
                    }
                    return true;
                case R.id.navigation_carts:
                    if (currentFragment != R.id.navigation_carts) {
                        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                        Fragment mFrag3 = new CartFragment();
                        t.replace(R.id.frameLayout, mFrag3);
                        t.commit();
                        currentFragment = R.id.navigation_carts;
                    }
                    return true;
                case R.id.navigation_favourites:
                    if (currentFragment != R.id.navigation_favourites) {
                        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                        Fragment mFrag4 = new FavouriteFragment();
                        t.replace(R.id.frameLayout, mFrag4);
                        t.commit();
                        currentFragment = R.id.navigation_favourites;
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Constraints.getCategory(getApplicationContext());


        if (!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        } else {
            Constraints.currentUser = new User(
                    SharedPrefManager.getInstance(this).getUserID(),
                    SharedPrefManager.getInstance(this).getUserEmail(),
                    SharedPrefManager.getInstance(this).getUserName()
            );
        }

        navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        frameLayout = findViewById(R.id.frameLayout);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, new HomeFragment());
        ft.commit();


        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) navView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(3); // number of menu from left
        int quantity = new DatabaseHandler(getApplicationContext()).getCountCart(Constraints.currentUser.getId());
        if (quantity>0){
            new QBadgeView(this).bindTarget(v).setBadgeNumber(quantity);
        }

    }

    @Override
    protected void onResume() {
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) navView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(3); // number of menu from left
        int quantity = new DatabaseHandler(getApplicationContext()).getCountCart(Constraints.currentUser.getId());
        if (quantity>0){
            new QBadgeView(this).bindTarget(v).setBadgeNumber(quantity);
        }

        if (!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        } else {
            Constraints.currentUser = new User(
                    SharedPrefManager.getInstance(this).getUserID(),
                    SharedPrefManager.getInstance(this).getUserEmail(),
                    SharedPrefManager.getInstance(this).getUserName()
            );
        }
//        if (!Constraints.isConnectedToInternet(this)){
//            finish();
//            startActivity(new Intent(this, NetConnectionFailedActivity.class));
//        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (doubleClickedExit){
            super.onBackPressed();
            return;
        }

        this.doubleClickedExit = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleClickedExit = false;
            }
        }, 2000);
    }

}
