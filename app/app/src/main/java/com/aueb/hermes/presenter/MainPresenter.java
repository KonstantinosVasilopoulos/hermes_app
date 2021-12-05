package com.aueb.hermes.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.aueb.hermes.utils.RegisterDeviceRequestBody;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;

public class MainPresenter {

    private Context context;
    private SharedPreferences sharedPreferences;

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
                    URL url = new URL("localhost:8080/register-device");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    String data = "{\"uuid\": \"" + uuid + "\", \"antennaBatteryConsumption\": \"" + antennaBatteryConsumption + "\"}";
                    try(OutputStream os = con.getOutputStream()) {
                        byte[] input = data.getBytes();
                        os.write(input, 0, input.length);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        thread.start();
    }

}
