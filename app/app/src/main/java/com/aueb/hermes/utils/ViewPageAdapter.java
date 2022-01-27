package com.aueb.hermes.utils;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aueb.hermes.R;
import com.aueb.hermes.view.StatisticsFragment;

public class ViewPageAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public ViewPageAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, ViewPageAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new StatisticsFragment();;
        Bundle args = new Bundle();
        //FRAGMENT_TYPE : true = personal, false = average
        if (position == 0){
            args.putBoolean("FRAGMENT_TYPE", true);
        }
        else {
            args.putBoolean("FRAGMENT_TYPE", false);
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title = null;
        if (position == 0){
            title = mContext.getText(R.string.personal_tab_text);
        }
        else if (position == 1){
            title = mContext.getText(R.string.avarage_tab_text);
        }
        return title;
    }
}
