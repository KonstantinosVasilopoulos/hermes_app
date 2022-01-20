package com.aueb.hermes.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aueb.hermes.R;
import com.aueb.hermes.presenter.PersonalStatisticsPresenter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class PersonalStatisticsFragment extends Fragment {

    private LineGraphSeries<DataPoint> seriesPersonalNetwork;


    public PersonalStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        PersonalStatisticsPresenter personalStatisticsPresenter = new PersonalStatisticsPresenter(this, sharedPreferences);
        personalStatisticsPresenter.getStatistics("network/20-01-2022-05/1/764e8ee1-7313-4810-a8b7-72a2444445ad/com-samsung-ucs-agent-boot");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_statistics, container, false);
    }

    @Override
    public void onStart() {

        super.onStart();
    }


    public void setSeriesPersonalNetwork( LineGraphSeries<DataPoint> seriesPersonalNetwork){
        this.seriesPersonalNetwork = seriesPersonalNetwork;
    }

}