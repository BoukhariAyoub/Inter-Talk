package com.aboukhari.intertalking.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.activity.main.Test;
import com.aboukhari.intertalking.activity.registration.RegisterBasic;
import com.aboukhari.intertalking.adapter.RegistrationTabsAdapter;

import java.util.ArrayList;

import github.chenupt.springindicator.viewpager.ScrollerViewPager;


public class SpringIndicator extends FragmentActivity implements OnClickListener {

    ScrollerViewPager viewPager;
    Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_indicator);

        viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);
        mNextButton = (Button) findViewById(R.id.btn_next);
        github.chenupt.springindicator.SpringIndicator springIndicator = (github.chenupt.springindicator.SpringIndicator) findViewById(R.id.indicator);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);*/

        mNextButton.setOnClickListener(this);

        viewPager.setAdapter(new RegistrationTabsAdapter(getSupportFragmentManager(), getFragments(), getTitles()));
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

        fragments.add(new Test());
        fragments.add(new RegisterBasic());
        fragments.add(new Test());
        return fragments;
    }
    
    private void next(){
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    private void previous(){
        viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
    }

    @Override
    public void onClick(View v) {
        if(v == mNextButton){
            next();
        }

    }
}