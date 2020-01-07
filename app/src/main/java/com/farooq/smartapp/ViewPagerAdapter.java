package com.farooq.smartapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.farooq.smartapp.fragment.GeneralFragment;
import com.farooq.smartapp.fragment.InstrumentFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new InstrumentFragment();
        } else if (position == 1) {
            fragment = new GeneralFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Instrument";
        } else if (position == 1) {
            title = "General";
        }
        return title;
    }
}
