package com.dharma.shopinar.networksync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

public class CheckInternetConnection
        extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return;
        } else {
            Log.e("CONNECTION","NO internet please connect to wifi");


            new AestheticDialog.Builder((Activity) context, DialogStyle.CONNECTIFY, DialogType.ERROR)
                    .setTitle("No Internet")
                    .setMessage("Cannot connect to servers !")
                    .setCancelable(false)
                    .setDarkMode(false)
                    .setGravity(Gravity.CENTER)
                    .setAnimation(DialogAnimation.DEFAULT)
                    .show();



            return;

        }

    }
}

