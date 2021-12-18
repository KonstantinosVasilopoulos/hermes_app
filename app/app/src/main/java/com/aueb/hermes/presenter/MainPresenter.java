package com.aueb.hermes.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

}
