package com.aueb.hermes.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

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

public class PersonalStatisticsFragment extends Fragment {

    private LineGraphSeries<DataPoint> mPersonalNetworkSeries;
    private LinearLayout mScrollLayout;
    private boolean viewCreated;

    public PersonalStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        PersonalStatisticsPresenter personalStatisticsPresenter = new PersonalStatisticsPresenter(this, sharedPreferences);
        personalStatisticsPresenter.getStatistics("network/21-01-2022-05/1/e5236952-79fd-41a3-9229-fe3950d5c265/com-samsung-android-scloud", "android.intent.action.PERSONAL_NETWORK_FINISHED");

        mScrollLayout = this.getActivity().findViewById(R.id.personal_statistics_layout);
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

        viewCreated = true;
    }

    // Replace the network loading spinner with the network graph
    public void displayNetworkGraph() {
        // TODO: This method is called before onStart!
        while (!viewCreated) {
            continue;
        }
        //  Remove the loading spinner
        ProgressBar loadingSpinner = getView().findViewById(R.id.personalNetworkProgressBar);
        mScrollLayout.removeView(loadingSpinner);

        // Add a new graph
        GraphView graph = new GraphView(this.getActivity());
        graph.addSeries(mPersonalNetworkSeries);
        mScrollLayout.addView(graph);
    }

    // Setter
    public void setPersonalNetworkSeries(LineGraphSeries<DataPoint> mPersonalNetworkSeries){
        this.mPersonalNetworkSeries = mPersonalNetworkSeries;
    }
}