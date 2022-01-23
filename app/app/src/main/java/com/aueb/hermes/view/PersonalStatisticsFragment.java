package com.aueb.hermes.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.aueb.hermes.R;
import com.aueb.hermes.presenter.PersonalStatisticsPresenter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Iterator;

public class PersonalStatisticsFragment extends Fragment {

    private volatile LineGraphSeries<DataPoint> mPersonalNetworkSeries;
    private LinearLayout mScrollLayout;
    private boolean mDisplayNetworkGraph;

    public PersonalStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        PersonalStatisticsPresenter personalStatisticsPresenter = new PersonalStatisticsPresenter(this, sharedPreferences);
        personalStatisticsPresenter.getStatistics("network/23-01-2022-03/1/1ae50294-501e-4cd2-9eed-608135ce5e0e/com-samsung-android-SettingsReceiver", "android.intent.action.PERSONAL_NETWORK_FINISHED");
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

        mScrollLayout = getView().findViewById(R.id.personal_statistics_layout);

        while (mPersonalNetworkSeries == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        displayNetworkGraph();
        for (Iterator<DataPoint> it = mPersonalNetworkSeries.getValues(0, 1); it.hasNext();) {
            DataPoint point = it.next();
            Log.d("personal", point.toString());
        }
    }

    // Replace the network loading spinner with the network graph
    public void displayNetworkGraph() {
        //  Remove the loading spinner
        ProgressBar loadingSpinner = getView().findViewById(R.id.personalNetworkProgressBar);
        mScrollLayout.removeView(loadingSpinner);

        // Add a new graph
        GraphView graph = getView().findViewById(R.id.personal_network_graph);
        //graph.addSeries(mPersonalNetworkSeries);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setTitle("Random Curve 1");
        series.setColor(Color.GREEN);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);
    }

    // Getters & setters
    public void setPersonalNetworkSeries(LineGraphSeries<DataPoint> mPersonalNetworkSeries){
        this.mPersonalNetworkSeries = mPersonalNetworkSeries;
    }

    public void setDisplayNetworkGraph(boolean value) {
        mDisplayNetworkGraph = value;
    }
}