package com.aboukhari.intertalking.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.activity.registration.RegisterBirthday;
import com.aboukhari.intertalking.activity.registration.RegisterGender;
import com.aboukhari.intertalking.activity.registration.RegisterImage;
import com.aboukhari.intertalking.activity.registration.RegisterLanguageKnown;
import com.aboukhari.intertalking.activity.registration.RegisterLanguageWanted;
import com.aboukhari.intertalking.activity.registration.RegisterName;
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
    ImageView mNextFragmentImageView;
    RegistrationTabsAdapter tabsAdapter;
    User mUser;
    String mPassword,mUid,mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);


        mPassword = getIntent().getStringExtra("password");
        mUid = getIntent().getStringExtra("uid");
        mEmail = getIntent().getStringExtra("email");
        mUser = Utils.getUserFromPreferences(this);

        mNextFragmentImageView = (ImageView) findViewById(R.id.iv_next_fragment);
        viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);

        tabsAdapter = new RegistrationTabsAdapter(getSupportFragmentManager(), getFragments(), getTitles());
        github.chenupt.springindicator.SpringIndicator springIndicator = (github.chenupt.springindicator.SpringIndicator) findViewById(R.id.indicator);

        viewPager.setAdapter(tabsAdapter);
        viewPager.fixScrollSpeed();

        // just set viewPager
        springIndicator.setViewPager(viewPager);
        mNextFragmentImageView.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == tabsAdapter.getCount() - 1) {
                    mNextFragmentImageView.setImageDrawable(ContextCompat.getDrawable(Main2Activity.this,R.drawable.ic_check_white_36dp));
                } else {
                    mNextFragmentImageView.setImageDrawable(ContextCompat.getDrawable(Main2Activity.this,R.drawable.ic_navigate_next_white_36dp));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == mNextFragmentImageView.getId()) {

            if (viewPager.getCurrentItem() == tabsAdapter.getCount() - 1) {
                registerUser();
            }
            else {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        }
    }


    public void goToNextFragment(){
        if (viewPager.getCurrentItem() == tabsAdapter.getCount() - 1) {
            registerUser();
        }
        else {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+ 1);
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
        titles.add("7");
        titles.add("8");

        return titles;
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new RegisterPassword());
        fragments.add(new RegisterName());
        fragments.add(new RegisterBirthday());
        fragments.add(new RegisterGender());
        fragments.add(new RegisterPlace());
        fragments.add(new RegisterLanguageKnown());
        fragments.add(new RegisterLanguageWanted());
        fragments.add(new RegisterImage());
        return fragments;
    }

    private void registerUser() {
        RegisterPassword registerPassword = (RegisterPassword) tabsAdapter.getItem(0);
        RegisterName registerName = (RegisterName) tabsAdapter.getItem(1);
        RegisterBirthday registerBirthday = (RegisterBirthday) tabsAdapter.getItem(2);
        RegisterGender registerGender = (RegisterGender) tabsAdapter.getItem(3);
        RegisterPlace registerPlace = (RegisterPlace) tabsAdapter.getItem(4);
        RegisterLanguageKnown registerLanguageKnown = (RegisterLanguageKnown) tabsAdapter.getItem(5);
        RegisterLanguageWanted registerLanguageWanted = (RegisterLanguageWanted) tabsAdapter.getItem(6);
        RegisterImage registerImage = (RegisterImage) tabsAdapter.getItem(7);

        String password = registerPassword.getPassword();
        String name = registerName.getName();
        Date birthDate = registerBirthday.getBirthdate();
        String gender = registerGender.getGender();
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


        FireBaseManager.getInstance(this).updateFirstLoginProfile(user,mPassword,password,bitmap,placeId,known,wanted);

    }

}
