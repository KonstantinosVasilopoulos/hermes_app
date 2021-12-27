package com.aueb.hermes;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aueb.hermes.presenter.MainPresenter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        String lastStr = sharedPreferences.getString("last", null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime last;
        if (lastStr == null) {
            last = LocalDateTime.now();
        } else {
            last = LocalDateTime.parse(lastStr, formatter);
        }
        presenter.collectAndSendRawData(last);

        // Update the variable storing the date and time the server was informed
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last", LocalDateTime.now().format(formatter));
        editor.apply();
    }
}