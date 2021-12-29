package com.aueb.hermes.utils;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.net.ConnectivityManager;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class QueryNetworkDetailsWorker implements Runnable {
    private final Map<String, Float> appUsages;
    private final LocalDateTime timeSlot;
    private final NetworkStatsManager networkStatsManager;
    private final Map<String, Integer> apps;
    private final String subscriberId;

    // Return value
    private volatile Map<String, Float> networkData;

    public QueryNetworkDetailsWorker(Map<String, Float> appUsages, LocalDateTime timeSlot,
                                     NetworkStatsManager networkStatsManager, Map<String, Integer> apps,
                                     String subscriberId) {
        this.appUsages = appUsages;
        this.timeSlot = timeSlot;
        this.networkStatsManager = networkStatsManager;
        this.apps = apps;
        this.subscriberId = subscriberId;
        this.networkData = new HashMap<>();
    }

    @Override
    public void run() {
        // For each app
        NetworkStats details;
        for (String app : appUsages.keySet()) {
            // Query the network stats manager for usage details
            try {
                long start = timeSlot.toEpochSecond(ZoneOffset.of("+2")) * 1000L;
                long end = timeSlot.plusHours(1).minusSeconds(1).toEpochSecond(ZoneOffset.of("+2")) * 1000L;
                Log.d("Network", start + " - " + end);
                details = networkStatsManager.queryDetailsForUid(
                        ConnectivityManager.TYPE_WIFI,
                        subscriberId,
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
                Log.d("Network", String.valueOf(traffic));
            } while (details.hasNextBucket());
            details.close();

            // Convert bytes to MB
            appUsages.put(app, (float) (traffic / Math.pow(1024.0, 2.0)));
        }

        // Set the return value
        networkData = appUsages;
    }

    public LocalDateTime getTimeSlot() {
        return timeSlot;
    }

    public Map<String, Float> getNetworkData() {
        return networkData;
    }
}
