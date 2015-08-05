package com.aboukhari.intertalking.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by aboukhari on 23/07/2015.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> mFragments;

    public TabsPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int index) {

        return mFragments.get(index);

    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return mFragments.size();
    }
}
