package com.aueb.hermes.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public class AntennaMonitor extends Service {

    private Looper looper;
    private Handler handler;

    public AntennaMonitor() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}