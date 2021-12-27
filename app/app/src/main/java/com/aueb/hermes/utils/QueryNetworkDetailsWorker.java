package com.aueb.hermes.utils;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.net.NetworkCapabilities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class QueryNetworkDetailsWorker implements Runnable {
    private final Map<String, Float> appUsages;
    private final LocalDateTime timeSlot;
    private final NetworkStatsManager networkStatsManager;
    private final Map<String, Integer> apps;

    // Return value
    private volatile Map<String, Float> networkData;

    public QueryNetworkDetailsWorker(Map<String, Float> appUsages, LocalDateTime timeSlot,
                                     NetworkStatsManager networkStatsManager, Map<String, Integer> apps) {
        this.appUsages = appUsages;
        this.timeSlot = timeSlot;
        this.networkStatsManager = networkStatsManager;
        this.apps = apps;
        this.networkData = new HashMap<>();
    }

    @Override
    public void run() {
        ZoneId timezone = ZoneId.of("UTC");

        // For each app
        NetworkStats details;
        for (String app : appUsages.keySet()) {
            // Query the network stats manager for usage details
            details = networkStatsManager.queryDetailsForUid(
                    NetworkCapabilities.TRANSPORT_WIFI,
                    null,
                    timeSlot.atZone(timezone).toEpochSecond(),
                    timeSlot.plusHours(1).minusSeconds(1).atZone(timezone).toEpochSecond(),
                    apps.get(app)
            );

            // Iterate the details to calculate the sum of all bytes received and transmitted
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            long traffic = 0L;
            while (details.hasNextBucket()) {
                details.getNextBucket(bucket);
                traffic += bucket.getRxBytes() + bucket.getTxBytes();
            }

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
