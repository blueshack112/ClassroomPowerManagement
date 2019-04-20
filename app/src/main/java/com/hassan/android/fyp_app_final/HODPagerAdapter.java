package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HODPagerAdapter extends FragmentPagerAdapter {
    private String [] titles = {"Room Status", "Requests","Override"};
    private Fragment [] fragments;
    public HODPagerAdapter (FragmentManager manager) {
        super(manager);
        fragments = new Fragment[2];
        fragments[0] = new FRGRoomStatusTab();
        fragments[1] = new FRGPendingRequestTab();
    }
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
