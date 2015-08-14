package com.aboukhari.intertalking.activity.profile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.adapter.LanguagesRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.aboukhari.intertalking.model.Language;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class ProfileView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        RecyclerView recyclerViewKnown = (RecyclerView) findViewById(R.id.recyclerViewKnown);
        RecyclerView recyclerViewWanted = (RecyclerView) findViewById(R.id.recyclerViewWanted);

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
       LanguagesRecyclerAdapter recyclerAdapter = new LanguagesRecyclerAdapter(this, getSelectedLanguages(), mLanguageAdapter);
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


}
