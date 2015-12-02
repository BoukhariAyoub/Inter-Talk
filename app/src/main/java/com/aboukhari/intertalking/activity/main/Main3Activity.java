package com.aboukhari.intertalking.activity.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

import java.util.ArrayList;


public class Main3Activity extends AppCompatActivity {

    private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Firebase.setAndroidContext(this);

        fireBaseManager = new FireBaseManager(this);
        FacebookSdk.sdkInitialize(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);


    }

    class SectionPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<>();

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(new Conversations());
            fragments.add(new Friends());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CHATS";
                case 1:
                    return "CONTACTS";
            }
            return null;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public FireBaseManager getFireBaseManager() {
        return fireBaseManager;
    }

    @Override
    public void onStart() {
        super.onStart();
        fireBaseManager.updateOnlineStatus();
    }
}
