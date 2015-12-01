package com.aboukhari.intertalking.activity.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.aboukhari.intertalking.R;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

public class Main3Activity extends FragmentActivity {
    private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);




        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);


    }

    class GoogleMusicAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<>();

        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);

            fragments.add(new Test());
            fragments.add(new Test());
        }

        @Override
        public Fragment getItem(int position) {
         return  fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
}
