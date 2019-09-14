package com.example.abeerfoodv0_0.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.abeerfoodv0_0.R;
import com.example.abeerfoodv0_0.utils.Constraints;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class NetConnectionFailedActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView refreshIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_connection_failed);

        initialization();
    }

    @SuppressLint("ResourceAsColor")
    private void initialization() {
        refreshIV = findViewById(R.id.refershIV);
        swipeRefreshLayout = findViewById(R.id.netConnectionSwipeToRefresh);

        refreshIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constraints.isConnectedToInternet(getApplicationContext())){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    DynamicToast.makeError(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if (Constraints.isConnectedToInternet(getApplicationContext())){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    DynamicToast.makeError(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
    }
}
