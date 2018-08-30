package com.narmware.samista.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.narmware.samista.R;
import com.narmware.samista.support.SharedPreferencesHelper;

public class SplashActivity extends AppCompatActivity {

    private static int TIMEOUT=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(SharedPreferencesHelper.getIsLogin(SplashActivity.this)==false)
                {
                    Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                }


                if(SharedPreferencesHelper.getIsLogin(SplashActivity.this)==true)
                {
                    Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        },TIMEOUT);
    }
}
