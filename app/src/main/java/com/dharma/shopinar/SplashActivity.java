package com.dharma.shopinar;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dharma.shopinar.usersession.UserSession;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    // To get user session data
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        session = new UserSession(SplashActivity.this);

        // Setting Font Style
        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        // Adding Animation
        YoYo.with(Techniques.Bounce)
                .duration(4000)
                .playOn(findViewById(R.id.logo));

        YoYo.with(Techniques.Bounce)
                .duration(4000)
                .playOn(findViewById(R.id.logo));

        YoYo.with(Techniques.FadeInUp)
                .duration(2000)
                .playOn(findViewById(R.id.appname));


        /*
         * Showing splash screen with a timer. This will be useful when we
         * want to show case app logo / company
         */

        new Handler(Looper.myLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start the app main activity
                finish();
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }, SPLASH_TIME_OUT);
    }
}