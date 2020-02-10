package com.farooq.smartapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.farooq.smartapp.MainActivity;
import com.farooq.smartapp.R;
import com.farooq.smartapp.ViewPagerAdapter;

public class SettingFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    FloatingActionButton floatingActionButton;
    MainActivity mainActivity;
    public SettingFragment() {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public SettingFragment(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Setting");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewPager);
        floatingActionButton = view.findViewById(R.id.fab);
        FragmentManager supportFragmentManager = mainActivity.getSupportFragmentManager();
        viewPagerAdapter = new ViewPagerAdapter(supportFragmentManager);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }



}
