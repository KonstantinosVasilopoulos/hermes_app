package com.aueb.hermes.view;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;

import com.aueb.hermes.R;
import com.aueb.hermes.utils.InitFinishedReceiver;
import com.aueb.hermes.utils.StatisticsReceiver;
import com.aueb.hermes.utils.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;


public class StatisticsDisplayActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPageAdapter mViewPageAdapter;
    private StatisticsReceiver mStatisticsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_display);

        //Connect tab layout with view pager

        mViewPager = findViewById((R.id.pager));
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPageAdapter = new ViewPageAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("android.intent.action.PERSONAL_NETWORK_FINISHED");
        mStatisticsReceiver = new StatisticsReceiver();
        registerReceiver(mStatisticsReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mStatisticsReceiver);
    }

}

