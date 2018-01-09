package com.pyozer.myagenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pyozer.myagenda.preferences.PrefManagerConfig;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);

        PrefManagerConfig prefManager = new PrefManagerConfig(this);
        if(prefManager.isFirstTimeLaunch()) {
            intent = new Intent(this, IntroActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
