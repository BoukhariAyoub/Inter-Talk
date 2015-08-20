package com.aboukhari.intertalking.activity.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.activity.FindUsers;
import com.aboukhari.intertalking.adapter.TabsPagerAdapter;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener {

    private FireBaseManager fireBaseManager;
    private Firebase ref;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    // Tab titles
    final int[] ICONS = new int[]{
            R.mipmap.ic_conversations,
            R.mipmap.ic_friends,
            R.mipmap.ic_settings,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        ref = new Firebase(getString(R.string.firebase_url));

        fireBaseManager = new FireBaseManager(this);
        FacebookSdk.sdkInitialize(this);



        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();

        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new Conversations());
        fragments.add(new Friends());
        fragments.add(new Test());

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(mAdapter);

        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (int icon : ICONS) {
            actionBar.addTab(actionBar.newTab().setIcon(getResources().getDrawable(icon))
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sync_fb_friends) {
            fireBaseManager.syncFacebookFriends(AccessToken.getCurrentAccessToken());
            //  Utils.exportDB("intertalk");
        }
        if (id == R.id.action_dummy_friends) {
            fireBaseManager.addGeneratedUsers();
        }

        if (id == R.id.action_find_users) {
            Intent intent = new Intent(this,FindUsers.class);
            startActivity(intent);
        }

        if (id == R.id.action_log_out) {
            fireBaseManager.logout(this);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }


    public FireBaseManager getFireBaseManager() {
        return fireBaseManager;
    }
}
