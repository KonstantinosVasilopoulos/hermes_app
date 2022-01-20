package com.aueb.hermes.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aueb.hermes.view.StatisticsDisplayActivity;

public class StatisticsReceiver extends BroadcastReceiver {

    private static final String TAG = "StatisticsReceiver";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.getAction());
    }
}
