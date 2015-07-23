package com.aboukhari.intertalking.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aboukhari.intertalking.activity.Conversations;
import com.aboukhari.intertalking.activity.Friends;
import com.aboukhari.intertalking.activity.Test;

/**
 * Created by aboukhari on 23/07/2015.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new Conversations();
            case 1:
                // Games fragment activity
                return new Friends();
            case 2:
                // Movies fragment activity
                return new Test();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
}
