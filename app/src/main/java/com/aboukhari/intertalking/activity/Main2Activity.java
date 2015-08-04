package com.aboukhari.intertalking.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.activity.main.Test;
import com.aboukhari.intertalking.activity.registration.RegisterBasic;
import com.aboukhari.intertalking.adapter.TabsPagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

public class Main2Activity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ViewPager pager = (ViewPager)findViewById(R.id.pager);

        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new Test());
        fragments.add(new RegisterBasic());
        fragments.add(new Test());


        pager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager(),fragments));

        //Bind the title indicator to the adapter


        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.titles);
        indicator = indicator;
        indicator.setViewPager(pager);

        final float density = getResources().getDisplayMetrics().density;
        indicator.setBackgroundColor(Color.BLACK);
        indicator.setRadius(10 * density);
       /* indicator.setPageColor(0x880000FF);
        indicator.setFillColor(0xFF888888);
        indicator.setStrokeColor(0xFF000000);
        indicator.setStrokeWidth(2 * density);
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
