package com.aueb.hermes.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aueb.hermes.R;
import com.aueb.hermes.presenter.StatisticsPresenter;
import com.aueb.hermes.utils.TimeSlotUtils;
import com.google.android.material.snackbar.Snackbar;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GraphActivity extends AppCompatActivity {

    private volatile LineGraphSeries<DataPoint> mPersonalNetworkSeries;
    private ConstraintLayout mRoot;
    private boolean mDisplayingGraphs;
    private boolean mError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Assemble URL
        String app = getIntent().getStringExtra("app");
        int year = getIntent().getIntExtra("year", 2022);
        int month = getIntent().getIntExtra("month", 1);
        int day = getIntent().getIntExtra("day", 1);
        int hour = getIntent().getIntExtra("hour", 1);

        LocalDateTime start = LocalDateTime.of(year,month, day,hour, 0);
        start = TimeSlotUtils.getTimeSlotHour(start);
        year = start.getYear();
        month = start.getMonthValue();
        day = start.getDayOfMonth();
        hour = start.getHour();

        app = app.replaceAll("\\.", "-");

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString("PREF_UNIQUE_ID", "1ae50294-501e-4cd2-9eed-608135ce5e0 e");

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(start, now);
        int slots = (int) (duration.toHours() / 4);

        String url;

        if (getIntent().getBooleanExtra("type", true)) {
            url = "network/" + addInitZero(day) + "-"  + addInitZero(month) + "-"  + year + "-"  + addInitZero(hour) + "/" + slots + "/" + uuid + "/" + app;
        }else {
            url = "network-average/" + addInitZero(day) + "-"  + addInitZero(month) + "-"  + year + "-"  + addInitZero(hour) + "/" + slots + "/" + app;
        }

        StatisticsPresenter statisticsPresenter = new StatisticsPresenter(this, sharedPreferences);
        statisticsPresenter.getStatistics(url);


        setContentView(R.layout.activity_graph);
        mRoot = findViewById(R.id.personal_network_graph_layout);

        if (!mDisplayingGraphs) {
            while (mPersonalNetworkSeries == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //  Remove the loading spinner
            ProgressBar loadingSpinner = findViewById(R.id.personalNetworkProgressBar);
            mRoot.removeView(loadingSpinner);
            if (mError){
                //Snackbar.make(mRoot, "No data for the requested application.", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(this, "No data for the requested application.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                displayNetworkGraph(start);
            }

        }
    }

    // Replace the network loading spinner with the network graph
    public void displayNetworkGraph(LocalDateTime start) {
        // Add a new graph
        GraphView graph = new GraphView(this);
        graph.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                if (isValueX) {
                    return start.plusHours((long) (4 * value)).format(formatter);
                } else {
                    return super.formatLabel(value, false);
                }
            }
        });
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(mPersonalNetworkSeries.getHighestValueX());
        mPersonalNetworkSeries.setTitle("Network-Date");
        mPersonalNetworkSeries.setColor(Color.BLUE);
        mPersonalNetworkSeries.setDrawDataPoints(true);
        mPersonalNetworkSeries.setDataPointsRadius(10);
        mPersonalNetworkSeries.setThickness(8);
        GridLabelRenderer renderer = graph.getGridLabelRenderer();
        renderer.setHorizontalLabelsAngle(160);
        graph.addSeries(mPersonalNetworkSeries);
        mRoot.addView(graph);

        mDisplayingGraphs = true;
    }

    // Getters & setters
    public void setPersonalNetworkSeries(LineGraphSeries<DataPoint> mPersonalNetworkSeries){
        this.mPersonalNetworkSeries = mPersonalNetworkSeries;
    }

    public void setError(){
        mError = true;
    }

    private String addInitZero(int value){
        if (value < 10){
            return "0" + value;
        }
        return String.valueOf(value);
    }
}