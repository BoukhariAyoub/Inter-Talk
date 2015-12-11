package com.aboukhari.intertalking.activity.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.adapter.LanguagesRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.aboukhari.intertalking.model.Language;
import com.aboukhari.intertalking.model.User;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class ProfileView extends Activity implements View.OnClickListener {

    RecyclerView recyclerViewKnown, recyclerViewWanted;
    TextView mDisplayTextView, mPlaceTextView;
    ImageView mAvatarImageView;
    FloatingActionButton fabAddFriend, fabSendMessage;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view_2);

        user = getIntent().getParcelableExtra("user");


        mDisplayTextView = (TextView) findViewById(R.id.tv_display_name);
        mPlaceTextView = (TextView) findViewById(R.id.tv_place);
        mAvatarImageView = (ImageView) findViewById(R.id.iv_avatar);

        fabAddFriend = (FloatingActionButton) findViewById(R.id.fab_add_friend);
        fabSendMessage = (FloatingActionButton) findViewById(R.id.fab_send_message);

        fabAddFriend.setOnClickListener(this);
        fabSendMessage.setOnClickListener(this);

//TOOD

        mDisplayTextView.setText(user.getDisplayName());
     //   mPlaceTextView.setText(user.getPlaceId() + " , " + user.getPlaceName());
        Utils.loadImage(user.getImageUrl(), mAvatarImageView);


        recyclerViewKnown = (RecyclerView) findViewById(R.id.recyclerViewKnown);
        recyclerViewWanted = (RecyclerView) findViewById(R.id.recyclerViewWanted);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }


        setupRecyclerView(recyclerViewKnown);
        setupRecyclerView(recyclerViewWanted);
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(false);
        Language[] lang = new Language[1];
        ArrayAdapter<Language> mLanguageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lang);
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());
        LanguagesRecyclerAdapter recyclerAdapter = new LanguagesRecyclerAdapter(this, getSelectedLanguages(), mLanguageAdapter, false);
        recyclerView.setAdapter(recyclerAdapter);
    }


    private ArrayList<Language> getSelectedLanguages() {
        ArrayList<Language> languages = new ArrayList<>();

        HashMap<String, Integer> langs = user.getKnownLanguages();
        if (langs != null) {
            for (String lang : langs.keySet()) {
                int level = langs.get(lang);
                languages.add(new Language(lang, level));
            }
        }


        return languages;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {

        Fade fade = new Fade();
        fade.setDuration(2000);
        getWindow().setReenterTransition(fade);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == fabAddFriend.getId()) {
            FireBaseManager.getInstance(this).addFriend(user);
        }

        if (v.getId() == fabSendMessage.getId()) {
            FireBaseManager.getInstance(this).createRoom(user);
        }
    }
}
