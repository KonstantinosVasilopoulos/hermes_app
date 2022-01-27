package com.aueb.hermes.view;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import com.aueb.hermes.R;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PersonalStatisticsFragment extends Fragment {

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    ArrayAdapter<String> mAdapter;

    public PersonalStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart(){

        super.onStart();

        //Pass application names to dropdown
        PackageManager packageManager = getActivity().getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        String[] names = new String[apps.size()];
        int i = 0;
        for (ApplicationInfo app :apps){
            names[i++] = app.processName;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, names);
        Spinner applicationDropdown = getView().findViewById(R.id.application_dropdown);
        applicationDropdown.setAdapter(adapter);

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        getView().findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {

            int year;
            int month;
            int day;
            int hour;

            @Override
            public void onClick(View view) {

                //Retrieve the chosen application
                String app = applicationDropdown.getSelectedItem().toString();


                DatePicker datePicker = (DatePicker) getView().findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) getView().findViewById(R.id.time_picker);

                year = datePicker.getYear();
                month = datePicker.getMonth() + 1;
                day = datePicker.getDayOfMonth();
                hour = timePicker.getHour();

                Intent intent = new Intent(getActivity(), PersonalGraphActivity.class);
                intent.putExtra("app", app);
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", day);
                intent.putExtra("hour", hour);
                startActivity(intent);

            }});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_statistics, container, false);
    }


}