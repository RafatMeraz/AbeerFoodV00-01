package com.example.abeerfoodv0_0.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.abeerfoodv0_0.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private final int DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Timer RunSplash = new Timer();
        TimerTask showSplash = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        } ;

        RunSplash.schedule(showSplash,DELAY);

    }
}
