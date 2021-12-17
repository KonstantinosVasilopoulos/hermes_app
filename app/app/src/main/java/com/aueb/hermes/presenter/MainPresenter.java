package com.aueb.hermes.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

                //send data to server
                //RegisterDeviceRequestBody data = new RegisterDeviceRequestBody(uuid, antennaBatteryConsumption);

                try {
                    URL url = new URL("http://192.168.68.110:8080/register-device");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    //to do
                    //Log.d("conection", String.valueOf(con.getResponseCode()));
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    // Package data into a JSON object
                    String data = "{\"uuid\": \"asqfwrw\", \"antennaBatteryConsumption\": 2.0}";

                    // Write data to output stream
                    try (OutputStream os = con.getOutputStream()) {
                        byte[] bytes = data.getBytes();
                        os.write(bytes, 0, bytes.length);
                        os.flush();
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
