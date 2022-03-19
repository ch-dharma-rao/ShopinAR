package com.dharma.shopinar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.dharma.shopinar.usersession.UserSession;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.dharma.shopinar.LoginActivity.USERID;

public class WelcomeActivity extends AppCompatActivity {
    WelcomeViewPagerAdapter welcomeViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;
    private ViewPager screenPager;
    private UserSession prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Checking for first time launch - before calling setContentView()
        prefManager = new UserSession(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        UserSession userSession = new UserSession(this);

        // Make the activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // When this activity is about to be launch we need to check if its openened before or not

//        if (restorePrefData()) {
//
//            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(loginActivity);
//            finish();
//
//
//        }

        setContentView(R.layout.activity_welcome);

        // Hide the action bar
//        getSupportActionBar().hide();

        // Initialize views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        // Fill list screen

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem(getString(R.string.slide_1_title), getString(R.string.slide_1_desc), getString(R.string.slide_1_anim)));
        mList.add(new ScreenItem(getString(R.string.slide_2_title), getString(R.string.slide_2_desc), getString(R.string.slide_2_anim)));
        mList.add(new ScreenItem(getString(R.string.slide_3_title), getString(R.string.slide_3_desc), getString(R.string.slide_3_anim)));
        mList.add(new ScreenItem(getString(R.string.slide_4_title), getString(R.string.slide_4_desc), getString(R.string.slide_4_anim)));
        mList.add(new ScreenItem(getString(R.string.slide_5_title), getString(R.string.slide_5_desc), getString(R.string.slide_5_anim)));


        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        welcomeViewPagerAdapter = new WelcomeViewPagerAdapter(this, mList);
        screenPager.setAdapter(welcomeViewPagerAdapter);

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {

                    position++;
                    screenPager.setCurrentItem(position);


                }

                if (position == mList.size() - 1) { // when we rech to the last screen

                    // TODO : show the GETSTARTED Button and hide the indicator and the next button

                    loaddLastScreen();


                }


            }
        });

        // tablayout add change listener


        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size() - 1) {
                    loaddLastScreen();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open main activity
                Intent mainActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mainActivity);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                prefManager.setFirstTimeLaunch(false);
//                savePrefsData();
                finish();


            }
        });

        // skip button click listener
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });


    }


    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        if (prefManager.isLoggedIn()) {
            String userId = prefManager.getUserID();
            Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
            i.putExtra(USERID,userId);
            startActivity(i);
        } else {
            Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(i);
        }
        finish();
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);

    }
}