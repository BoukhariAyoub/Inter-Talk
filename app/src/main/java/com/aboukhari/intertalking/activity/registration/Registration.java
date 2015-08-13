package com.aboukhari.intertalking.activity.registration;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.adapter.TabsPagerAdapter;
import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

import java.util.ArrayList;

public class Registration extends AppCompatActivity implements
        ActionBar.TabListener, View.OnClickListener {

    private FireBaseManager fireBaseManager;
    private Firebase ref;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private Button mNext;

    final int[] ICONS = new int[]{
            R.mipmap.ic_conversations,
            R.mipmap.ic_friends,
            R.mipmap.ic_settings,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Firebase.setAndroidContext(this);
        ref = new Firebase(getString(R.string.firebase_url));

        fireBaseManager = new FireBaseManager(this);
        FacebookSdk.sdkInitialize(this);


        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();

        mNext = (Button) findViewById(R.id.btn_next);
        mNext.setOnClickListener(this);

        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new RegisterBasic());
        fragments.add(new RegisterLanguages());


        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        /*try {
            Drawable d = Drawable.createFromStream(getAssets().open("bg.jpg"), null);
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            pager.setBackgroundDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onClick(View v) {
        if (v == mNext) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        }
    }

    public static void setTabColor(TabHost tabhost) {
        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FF0000")); //unselected
        }
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#0000FF")); // selected
    }
}
