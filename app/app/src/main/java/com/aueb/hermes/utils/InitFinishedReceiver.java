package com.aueb.hermes.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aueb.hermes.MainActivity;
import com.aueb.hermes.view.StatisticsDisplayActivity;

public class InitFinishedReceiver extends BroadcastReceiver {

    private static final String TAG = "InitFinishedReceiver";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        //redirect to app's display page
        Intent displayIntent = new Intent(context, StatisticsDisplayActivity.class);
        context.startActivity(displayIntent);
        MainActivity a = (MainActivity) context;
        a.kill();
    }
}
