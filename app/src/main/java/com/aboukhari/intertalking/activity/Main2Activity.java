package com.aboukhari.intertalking.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.activity.registration.RegisterFusion;
import com.aboukhari.intertalking.activity.registration.RegisterImage;
import com.aboukhari.intertalking.activity.registration.RegisterLanguageKnown;
import com.aboukhari.intertalking.activity.registration.RegisterLanguageWanted;
import com.aboukhari.intertalking.activity.registration.RegisterPassword;
import com.aboukhari.intertalking.activity.registration.RegisterPlace;
import com.aboukhari.intertalking.adapter.RegistrationTabsAdapter;
import com.aboukhari.intertalking.model.Language;
import com.aboukhari.intertalking.model.User;

import java.util.ArrayList;
import java.util.Date;

import github.chenupt.springindicator.viewpager.ScrollerViewPager;

public class Main2Activity extends FragmentActivity implements View.OnClickListener {

    ScrollerViewPager viewPager;
    Button mNextButton;
    RegistrationTabsAdapter tabsAdapter;
    User mUser;
    String mPassword, mUid, mEmail;
    TextView mNextFragment;

    RegisterPassword registerPassword ;
    RegisterFusion registerFusion;
    RegisterPlace registerPlace;
    RegisterLanguageKnown registerLanguageKnown;
    RegisterLanguageWanted registerLanguageWanted;
    RegisterImage registerImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);


        mPassword = getIntent().getStringExtra("password");
        mUid = getIntent().getStringExtra("uid");
        mEmail = getIntent().getStringExtra("email");
        mUser = Utils.getUserFromPreferences(this);

        mNextFragment = (TextView) findViewById(R.id.tv_next_fragement);

        viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);

        tabsAdapter = new RegistrationTabsAdapter(getSupportFragmentManager(), getFragments(), getTitles());
        github.chenupt.springindicator.SpringIndicator springIndicator = (github.chenupt.springindicator.SpringIndicator) findViewById(R.id.indicator);

        viewPager.setAdapter(tabsAdapter);
        viewPager.fixScrollSpeed();

        // just set viewPager
        springIndicator.setViewPager(viewPager);
        mNextFragment.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int icon;
                String text;
                if (position == tabsAdapter.getCount() - 1) {
                    icon = R.drawable.ic_done_white_24dp;
                    text = "DONE";
                } else {
                    icon = R.drawable.ic_arrow_forward_white_24dp;
                    text = "NEXT";

                }

                mNextFragment.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0);
                mNextFragment.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == mNextFragment.getId()) {
            goToNextFragment();
        }
    }


    private ArrayList<String> getTitles() {
        ArrayList<String> titles = new ArrayList<>();
        titles.add("1");
        titles.add("2");
        titles.add("3");
        titles.add("4");
        titles.add("5");
        titles.add("6");


        return titles;
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();


       registerPassword = new RegisterPassword();
        registerFusion = new RegisterFusion();
        registerPlace = new RegisterPlace();
        registerLanguageKnown = new RegisterLanguageKnown();
        registerLanguageWanted = new RegisterLanguageWanted();
        registerImage = new RegisterImage();
        fragments.add(registerPassword);
        fragments.add(registerFusion);
        fragments.add(registerPlace);
        fragments.add(registerLanguageKnown);
        fragments.add(registerLanguageWanted);
        fragments.add(registerImage);
        return fragments;
    }

    private void registerUser() {

        String password = registerPassword.getPassword();
        String name = registerFusion.getName();
        Date birthDate = registerFusion.getBirthdate();
        String gender = registerFusion.getGender();
        String placeId = registerPlace.getPlaceId();
        Bitmap bitmap = registerImage.getBitmap();

        User user = new User();
        user.setUid(mUid);
        user.setEmail(mEmail);
        user.setDisplayName(name);
        user.setBirthday(birthDate);
        user.setGender(gender);


        ArrayList<Language> known = registerLanguageKnown.getKnownLanguages();
        ArrayList<Language> wanted = registerLanguageWanted.getWantedLanguages();


        FireBaseManager.getInstance(this).updateFirstLoginProfile(user, mPassword, password, bitmap, placeId, known, wanted);

    }


    public void goToNextFragment() {
        if (viewPager.getCurrentItem() == tabsAdapter.getCount() - 1) {
            registerUser();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

}
