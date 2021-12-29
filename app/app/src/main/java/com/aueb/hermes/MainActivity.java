package com.aueb.hermes;

import android.app.AppOpsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.aueb.hermes.presenter.MainPresenter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check whether the android.permission.PACKAGE_USAGE_STATS has been granted
        AppOpsManager appOps = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED) {
            // Request the aforementioned permission
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        // On install
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        boolean registered = sharedPreferences.getBoolean("registered", false);

        MainPresenter presenter = new MainPresenter(this, sharedPreferences);

        if (!registered) {
            presenter.registerDevice();

            // Set the device as registered
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("registered", true);
            editor.apply();
        }

        // Collect and send the device's data to the server
        //String lastStr = sharedPreferences.getString("last", null);
        String lastStr = "29-12-2021 10:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime last;
        if (lastStr == null) {
            last = LocalDateTime.now();
        } else {
            last = LocalDateTime.parse(lastStr, formatter);
        }
        presenter.collectAndSendRawData(last);

        // Update the variable storing the date and time the server was informed
        last = LocalDateTime.now().minusHours(1);
        last = last.withMinute(0);
        last = last.withSecond(0);
        last = last.withNano(0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last", last.format(formatter));
        editor.apply();
    }
}