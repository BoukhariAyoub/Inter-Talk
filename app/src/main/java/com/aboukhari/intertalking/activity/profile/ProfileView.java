package com.aboukhari.intertalking.activity.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.adapter.LanguagesRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.aboukhari.intertalking.model.Language;
import com.aboukhari.intertalking.model.User;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class ProfileView extends Activity {

    RecyclerView recyclerViewKnown, recyclerViewWanted;
    TextView mDisplayTextView, mPlaceTextView;
    ImageView mAvatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view_2);

        User user = getIntent().getParcelableExtra("user");


        mDisplayTextView = (TextView) findViewById(R.id.tv_display_name);
        mPlaceTextView = (TextView) findViewById(R.id.tv_place);
        mAvatarImageView = (ImageView) findViewById(R.id.iv_avatar);

        mDisplayTextView.setText(user.getDisplayName());
        mPlaceTextView.setText(user.getPlaceId() + " , " + user.getPlaceName());
        Utils.setImage(this, user.getImageUrl(), mAvatarImageView);


        recyclerViewKnown = (RecyclerView) findViewById(R.id.recyclerViewKnown);
        recyclerViewWanted = (RecyclerView) findViewById(R.id.recyclerViewWanted);

        setupWindowAnimations();


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
        LanguagesRecyclerAdapter recyclerAdapter = new LanguagesRecyclerAdapter(this, getSelectedLanguages(), mLanguageAdapter,false);
        recyclerView.setAdapter(recyclerAdapter);
    }


    private ArrayList<Language> getSelectedLanguages() {
        ArrayList<Language> languages = new ArrayList<>();
        languages.add(new Language("en"));
        languages.add(new Language("ar"));
        languages.add(new Language("fr"));
        languages.add(new Language("en"));
        languages.add(new Language("ar"));
        languages.add(new Language("fr"));
        return languages;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Explode explode = new Explode();
        explode.setDuration(2000);
        getWindow().setExitTransition(explode);

        Fade fade = new Fade();
        fade.setDuration(2000);
        getWindow().setReenterTransition(fade);
    }

}
