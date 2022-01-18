package com.aueb.hermes.utils;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aueb.hermes.R;
import com.aueb.hermes.view.CombinedStatisticsFragment;
import com.aueb.hermes.view.PersonalStatisticsFragment;

public class ViewPageAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public ViewPageAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, ViewPageAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0){
            fragment = new PersonalStatisticsFragment();
        }
        else {
            fragment = new CombinedStatisticsFragment();
        }

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
