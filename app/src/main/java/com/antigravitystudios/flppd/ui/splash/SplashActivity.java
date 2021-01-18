package com.antigravitystudios.flppd.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.ui.home.HomeActivity;
import com.antigravitystudios.flppd.ui.main.MainActivity;
import com.antigravitystudios.flppd.ui.tutorial.TutorialActivity;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (SPreferences.isLogged()) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    if(getIntent().getExtras() != null) intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                } else if (!SPreferences.isTutorialShown()) {
                    startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
