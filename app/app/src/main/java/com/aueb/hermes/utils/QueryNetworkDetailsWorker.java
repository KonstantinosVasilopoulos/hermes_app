package com.aueb.hermes.utils;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.net.ConnectivityManager;
import android.os.Build;

import com.aueb.hermes.presenter.MainPresenter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class QueryNetworkDetailsWorker implements Runnable {
    private final Map<String, Long> appUsages;
    private final LocalDateTime timeSlot;
    private final NetworkStatsManager networkStatsManager;
    private final Map<String, Integer> apps;
    private final int TIME_SLOT_SIZE;


    // Return value
    private volatile Map<String, Long> networkData;

    public QueryNetworkDetailsWorker(Map<String, Long> appUsages, LocalDateTime timeSlot,
                                     NetworkStatsManager networkStatsManager, Map<String, Integer> apps, int TIME_SLOT_SIZE) {
        this.appUsages = appUsages;
        this.timeSlot = timeSlot;
        this.networkStatsManager = networkStatsManager;
        this.apps = apps;
        this.networkData = new HashMap<>();
        this.TIME_SLOT_SIZE = TIME_SLOT_SIZE;
    }

    @Override
    public void run() {
        // For each app
        NetworkStats details;
        for (String app : appUsages.keySet()) {
            // Query the network stats manager for usage details
            try {
                long start = timeSlot.toEpochSecond(ZoneOffset.of("+2")) * 1000L;
                long end = timeSlot.plusHours(TIME_SLOT_SIZE).minusSeconds(1).toEpochSecond(ZoneOffset.of("+2")) * 1000L;
                // Log.d("Network", start + " - " + end);
                details = networkStatsManager.queryDetailsForUid(
                        ConnectivityManager.TYPE_WIFI,
                        Build.VERSION.SDK_INT <= 30 ? "" : null,
                        start,
                        end,
                        apps.get(app)
                );
            } catch (SecurityException e) {
                details = null;
            }

            if (details == null) {
                continue;
            }

            // Iterate the details to calculate the sum of all bytes received and transmitted
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            long traffic = 0L;
            do {
                details.getNextBucket(bucket);
                traffic += bucket.getRxBytes() + bucket.getTxBytes();
            } while (details.hasNextBucket());
            details.close();

            appUsages.put(app, traffic);
        }

        // Set the return value
        networkData = appUsages;
    }

    public LocalDateTime getTimeSlot() {
        return timeSlot;
    }

    public Map<String, Long> getNetworkData() {
        return networkData;
    }
}
