package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//TODO: documentation

public class FRGHODTabPages extends Fragment {
    private TabLayout tabLayout;
    private HODPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private String userID;
    public FRGHODTabPages() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frghodtab_pages, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.hod_tabs);
        viewPager = view.findViewById(R.id.hod_tabs_pager);
        viewPagerAdapter = new HODPagerAdapter(getActivity().getSupportFragmentManager(), userID);


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setUserID (String userID) {
        this.userID = userID;
    }
}
