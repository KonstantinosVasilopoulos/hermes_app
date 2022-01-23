package com.aueb.hermes.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.aueb.hermes.R;
import com.aueb.hermes.presenter.PersonalStatisticsPresenter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class PersonalStatisticsFragment extends Fragment {

    private volatile LineGraphSeries<DataPoint> mPersonalNetworkSeries;
    private FrameLayout mRoot;
    private boolean mDisplayingGraphs;

    public PersonalStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        PersonalStatisticsPresenter personalStatisticsPresenter = new PersonalStatisticsPresenter(this, sharedPreferences);
        personalStatisticsPresenter.getStatistics("network/23-01-2022-00/4/b0bae7e7-fbfb-4a80-a475-da55b4955913/com-google-android-youtube");
//    2022-01-23 00:00:00 |         6174466759976182808 |         14823 | b0bae7e7-fbfb-4a80-a475-da55b4955913 | com.google.android.youtube
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

        mRoot = getView().findViewById(R.id.personal_fragment_root);

        if (!mDisplayingGraphs) {
            while (mPersonalNetworkSeries == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            displayNetworkGraph();
        }
    }

    // Replace the network loading spinner with the network graph
    public void displayNetworkGraph() {
        //  Remove the loading spinner
        ProgressBar loadingSpinner = getView().findViewById(R.id.personalNetworkProgressBar);
        mRoot.removeView(loadingSpinner);

        // Add a new graph
        GraphView graph = new GraphView(this.getActivity());
        graph.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(mPersonalNetworkSeries.getHighestValueX());
        mPersonalNetworkSeries.setTitle("Random Curve 1");
        mPersonalNetworkSeries.setColor(Color.BLUE);
        mPersonalNetworkSeries.setDrawDataPoints(true);
        mPersonalNetworkSeries.setDataPointsRadius(10);
        mPersonalNetworkSeries.setThickness(8);
        graph.addSeries(mPersonalNetworkSeries);
        mRoot.addView(graph);

        mDisplayingGraphs = true;
    }

    // Getters & setters
    public void setPersonalNetworkSeries(LineGraphSeries<DataPoint> mPersonalNetworkSeries){
        this.mPersonalNetworkSeries = mPersonalNetworkSeries;
    }
}