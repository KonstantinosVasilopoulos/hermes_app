package com.aueb.hermes.presenter;

import android.Manifest;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.aueb.hermes.utils.QueryNetworkDetailsWorker;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainPresenter {

    private final Context context;
    private final SharedPreferences sharedPreferences;

    // Constants
    private static final String BACKEND_IP_ADDRESS = "192.168.1.17:8080";
    private static final String BACKEND_REGISTER_URL = "http://" + BACKEND_IP_ADDRESS + "/register-device";

    public MainPresenter(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public void registerDevice() {
        Thread thread = new Thread(() -> {
            //generate random uuid
            String uuid;
            final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
            uuid = sharedPreferences.getString(PREF_UNIQUE_ID, null);
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREF_UNIQUE_ID, uuid);
                editor.apply();
            }

            //get the wifi's active state battery consumption
            float antennaBatteryConsumption = 2; //for testing only to be changed

            try {
                URL url = new URL(BACKEND_REGISTER_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                // Package data into a JSON object
                String data = "{\"uuid\": \"" + uuid + "\", \"antennaBatteryConsumption\": \"" + antennaBatteryConsumption + "\"}";

                // Write data to output stream
                try (OutputStream os = con.getOutputStream()) {
                    byte[] bytes = data.getBytes();
                    os.write(bytes, 0, bytes.length);
                    os.flush();
                    con.getResponseCode();
                }finally {
                    con.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    // Collect network statistics, calculate battery consumption
    // and send the data to the backend server
    public void collectAndSendRawData(LocalDateTime last) {
        // Network data
        Map<LocalDateTime, Map<String, Float>> networkUsagePerApp = collectNetworkStatistics(last);
        Log.d("Network", "NUPA: " + networkUsagePerApp.toString());
        // TODO: Battery data
    }

    // Collect network usage for each app
    private Map<LocalDateTime, Map<String, Float>> collectNetworkStatistics(LocalDateTime last) {
        // Data structure to store network usage
        Map<LocalDateTime, Map<String, Float>> networkUsagePerApp = new HashMap<>();

        // All the applications with internet access
        Map<String, Integer> apps = getApplications();

        // Initialize inner dictionaries
        LocalDateTime now = LocalDateTime.now();
        now = now.withMinute(0);
        now = now.withSecond(0);
        now = now.withNano(0);
        LocalDateTime current = last.plusHours(1);
        Map<String, Float> appUsages;
        while (current.isBefore(now)) {
            appUsages = new HashMap<>();
            for (String app : apps.keySet()) {
                appUsages.put(app, .0f);
            }

            networkUsagePerApp.put(current, appUsages);
            current = current.plusHours(1);
        }

        // Use the network stats manager to get the statistics in question
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
        Map<Thread, QueryNetworkDetailsWorker> workers = new HashMap<>();
        for (LocalDateTime timeSlot : networkUsagePerApp.keySet()) {
            // Delegate tasks to worker threads
            QueryNetworkDetailsWorker worker = new QueryNetworkDetailsWorker(
                    networkUsagePerApp.get(timeSlot),
                    timeSlot,
                    networkStatsManager,
                    apps,
                    Build.VERSION.SDK_INT <= 30 ? "" : null
            );
            Thread thread = new Thread(worker);
            workers.put(thread, worker);
            thread.start();
        }

        // Wait for the threads to finish and collect the produced data
        for (Thread thread : workers.keySet()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Store the new data
            QueryNetworkDetailsWorker worker = workers.get(thread);
            assert worker != null;
            networkUsagePerApp.put(worker.getTimeSlot(), worker.getNetworkData());
        }

        return networkUsagePerApp;
    }

    // Helper method that returns a dictionary of all available applications
    // Format: { name: uid  }
    private Map<String, Integer> getApplications() {
        Map<String, Integer> apps = new HashMap<>();

        //  Use the package manager to retrieve a list of all apps installed
        final PackageManager packageManager = this.context.getPackageManager();
        for (PackageInfo app : packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)) {
            if (app.requestedPermissions == null) continue;

            // Get only the apps which have internet permission
            for (String permission : app.requestedPermissions) {
                if (TextUtils.equals(permission, Manifest.permission.INTERNET)) {
                    apps.put(app.applicationInfo.processName, app.applicationInfo.uid);
                    break;
                }
            }
        }
        return apps;
    }
}
