package com.aueb.hermes.presenter;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainPresenter {

    private Context context;
    private SharedPreferences sharedPreferences;

    public MainPresenter(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public void registerDevice() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //generate random uuid
                String uuid = null;
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
                    URL url = new URL("http://10.0.2.2:8080/register-device");
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
            }
        });
        thread.start();
    }

    // Collect network statistics, calculate battery consumption
    // and send the data to the backend server
    public void collectAndSendRawData() {
        collectNetworkStatistics();
    }

    private void collectNetworkStatistics() {
        getApplications();
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
