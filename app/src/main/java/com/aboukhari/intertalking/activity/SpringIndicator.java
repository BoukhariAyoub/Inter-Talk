package com.aboukhari.intertalking.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.activity.registration.RegisterBasic;
import com.aboukhari.intertalking.activity.registration.RegisterLanguages;
import com.aboukhari.intertalking.adapter.RegistrationTabsAdapter;
import com.aboukhari.intertalking.model.User;

import java.util.ArrayList;

import github.chenupt.springindicator.viewpager.ScrollerViewPager;


public class SpringIndicator extends FragmentActivity implements OnClickListener {

    ScrollerViewPager viewPager;
    Button mNextButton;
    User mUser;
    public static Bitmap bitmap;
    RegistrationTabsAdapter tabsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_indicator);

        viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);
        mNextButton = (Button) findViewById(R.id.btn_next);


        github.chenupt.springindicator.SpringIndicator springIndicator = (github.chenupt.springindicator.SpringIndicator) findViewById(R.id.indicator);


        tabsAdapter = new RegistrationTabsAdapter(getSupportFragmentManager(), getFragments(), getTitles());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = tabsAdapter.getItem(position);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(tabsAdapter);
        viewPager.fixScrollSpeed();

        // just set viewPager
        springIndicator.setViewPager(viewPager);
    }


    private ArrayList<String> getTitles() {
        ArrayList<String> titles = new ArrayList<>();
        titles.add("1");
        titles.add("2");
        titles.add("3");

        return titles;
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new RegisterBasic());
        fragments.add(new RegisterLanguages());
        return fragments;
    }


    private void next() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    private void previous() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    @Override
    public void onClick(View v) {
        if (v == mNextButton) {
            next();
            SmsManager sm = SmsManager.getDefault();
            String number = "0033787499701";
            //  sm.sendTextMessage(number, null, "test", null, null);
        }
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }


    public void register() {

        RegisterBasic registerBasic = (RegisterBasic) tabsAdapter.getItem(0);
        RegisterLanguages registerLanguages = (RegisterLanguages) tabsAdapter.getItem(1);

        mUser = new User();
        mUser.setUid("new");
        registerBasic.register(mUser);
        registerLanguages.register(mUser);


    }


}
