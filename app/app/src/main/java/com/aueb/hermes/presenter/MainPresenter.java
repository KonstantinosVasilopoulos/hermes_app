package com.aueb.hermes.presenter;

import android.Manifest;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.aueb.hermes.utils.QueryNetworkDetailsWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class MainPresenter {

    private final Context context;
    private final SharedPreferences sharedPreferences;

    // Constants
    private static final String BACKEND_IP_ADDRESS = "192.168.1.20:8080";
    private static final String BACKEND_REGISTER_URL = "http://" + BACKEND_IP_ADDRESS + "/register-device";
    private static final String BACKEND_INIT_DATA_URL = "http://" + BACKEND_IP_ADDRESS + "/init-data/";
    public static final int TIME_SLOT_SIZE = 4;  // hours

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
            float antennaBatteryConsumption = 2.0f;

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
        Map<LocalDateTime, Map<String, Long>> networkUsagePerApp = collectNetworkStatistics(last);

        // TODO: Battery data
        Map<LocalDateTime, Map<String, Long>> batteryUsagePerApp = collectBatteryStatistics(networkUsagePerApp.keySet());

        // Package data before sending
        JSONArray packages = new JSONArray();
        JSONObject obj;
        for (LocalDateTime timeSlot : networkUsagePerApp.keySet()) {
            try {
                for (String app : networkUsagePerApp.get(timeSlot).keySet()) {
                    obj = new JSONObject();

                    // Convert time slot to string
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    String time = timeSlot.format(formatter);
                    obj.put("time", time);

                    // Put the rest of the data inside the JSON object
                    obj.put("app", app);
                    obj.put("bytes", networkUsagePerApp.get(timeSlot).get(app));
                    obj.put("consumption", batteryUsagePerApp.get(timeSlot).get(app));
                    packages.put(obj);
                }
            } catch (NullPointerException | JSONException e) {
                e.printStackTrace();
            }
        }

        // Send data to the backend server
        new Thread(() -> {
            try {
                // Get the device's UUID before sending
                final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
                String uuid = sharedPreferences.getString(PREF_UNIQUE_ID, null);
                if (uuid == null) {  // Device is not registered
                    return;
                }

                // Send POST request to backend server
                URL url = new URL(BACKEND_INIT_DATA_URL + uuid);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                // Write data to output stream
                try (OutputStream os = con.getOutputStream()) {
                    byte[] bytes = packages.toString().getBytes();
                    os.write(bytes, 0, bytes.length);
                    os.flush();
                    con.getResponseCode();
                } finally {
                    con.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Collect network usage for each app
    private Map<LocalDateTime, Map<String, Long>> collectNetworkStatistics(LocalDateTime last) {
        // Data structure to store network usage
        Map<LocalDateTime, Map<String, Long>> networkUsagePerApp = new HashMap<>();

        // All the applications with internet access
        Map<String, Integer> apps = getApplications();

        // Initialize inner dictionaries
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).minusHours(TIME_SLOT_SIZE);
        LocalDateTime current = last.plusHours(TIME_SLOT_SIZE);
        Map<String, Long> appUsages;
        // Current must be at least TIME_SLOT_SIZE hours before now
        while (current.isBefore(now) || current.equals(now)) {
            appUsages = new HashMap<>();
            for (String app : apps.keySet()) {
                appUsages.put(app, 0L);
            }

            networkUsagePerApp.put(current, appUsages);
            current = current.plusHours(TIME_SLOT_SIZE);
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
                    apps
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

    private Map<LocalDateTime, Map<String, Long>> collectBatteryStatistics(Set<LocalDateTime> timeSlots) {
        Map<LocalDateTime, Map<String, Long>> batteryUsagePerApp = new HashMap<>();
        Map<String, Long> appConsumptions;
        Random random = new Random();
        long consumption;
        for (LocalDateTime timeSlot : timeSlots) {
            appConsumptions = new HashMap<>();
            for (String app : getApplications().keySet()) {
                consumption = random.nextLong();
                if (consumption < 0) {
                    consumption *= -1L;
                }
                appConsumptions.put(app, consumption);
            }

            batteryUsagePerApp.put(timeSlot, appConsumptions);
        }

        return batteryUsagePerApp;
    }

    // Read the antenna's battery consumption from the power profile XML file
    // which is present in all Android devices
//    private float readAntennaBatteryConsumption() {
//        final String POWER_PROFILE_XML = "/android/platform/frameworks/base/core/res/res/xml/power_profile. xml";
//
//        // Open the XML file
//        Document powerProfile;
//        try {
//            File file = new File(POWER_PROFILE_XML);
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            powerProfile = db.parse(file);
//
//        } catch (IOException | ParserConfigurationException | SAXException e) {
//            Log.e("Power Profile", "Unable to open the XML file: " + e.toString());
//            return .0f;
//        }
//
//        // Extract the WiFi's active state's value
//        NodeList items = powerProfile.getElementsByTagName("item");
//        Log.d("Power Profile", items.toString());
//
//        return 0.f;
//    }
}
