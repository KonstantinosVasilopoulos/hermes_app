package com.aueb.hermes.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aueb.hermes.view.StatisticsDisplayActivity;

public class InitFinishedReceiver extends BroadcastReceiver {

    private static final String TAG = "InitFinishedReceiver";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("receiver", "called");
        //redirect to app's display page
        Intent displayIntent = new Intent(context, StatisticsDisplayActivity.class);
        //TODO: error with context
        context.startActivity(displayIntent);
    }
}
