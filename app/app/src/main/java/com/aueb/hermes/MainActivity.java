package com.aueb.hermes;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aueb.hermes.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on install
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        boolean registered = sharedPreferences.getBoolean("registered", false);

        MainPresenter presenter = new MainPresenter(this, sharedPreferences);

        if (!registered){
            presenter.registerDevice();

            // Set the device as registered
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("registered", true);
            editor.apply();
        }

        // Collect and send the device's data to the server
        presenter.collectAndSendRawData();
    }
}