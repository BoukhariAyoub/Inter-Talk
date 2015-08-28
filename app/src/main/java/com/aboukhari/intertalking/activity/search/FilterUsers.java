package com.aboukhari.intertalking.activity.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.JsonUtils;
import com.aboukhari.intertalking.adapter.CitiesAutoCompleteAdapter;
import com.aboukhari.intertalking.model.FilterPrefs;
import com.aboukhari.intertalking.model.Language;
import com.aboukhari.intertalking.model.Place;
import com.appyvet.rangebar.RangeBar;
import com.google.gson.Gson;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;

public class FilterUsers extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    ImageView mToolbarDone;
    ImageView mToolbarReturn;
    MaterialBetterSpinner mLanguagesSpinner;
    CitiesAutoCompleteAdapter mCityAdapter;
    Place mChosenPlace;
    Language language;
    AutoCompleteTextView mPlaceAutoComplete;
    RangeBar mRangeBar;
    SegmentedGroup mRadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_users);


        mPlaceAutoComplete = (AutoCompleteTextView) findViewById(R.id.auto_city);
        mToolbarDone = (ImageView) findViewById(R.id.toolbar_done);
        mRangeBar = (RangeBar) findViewById(R.id.rangebar);
        mRadioGroup = (SegmentedGroup) findViewById(R.id.radio_group);


        mCityAdapter = new CitiesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, Locale.getDefault().getLanguage());
        mPlaceAutoComplete.setThreshold(2);
        mPlaceAutoComplete.setAdapter(mCityAdapter);
        mPlaceAutoComplete.setOnItemClickListener(setPlacesOnClickListener());
        mPlaceAutoComplete.addTextChangedListener(this);


        mToolbarDone.setOnClickListener(this);


        mLanguagesSpinner = (MaterialBetterSpinner) findViewById(R.id.spinner_languages);
        ArrayAdapter<Language> dataAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, JsonUtils.jsonToLanguages(this));

        mLanguagesSpinner.setAdapter(dataAdapter);
      //  mLanguagesSpinner.getSe


        loadSettings();

    }



    @Override
    public void onClick(View v) {
        if (v.getId() == mToolbarDone.getId()) {
            saveSettings();
            Intent intent = new Intent(this, FindUsers.class);
            startActivity(intent);
        }
    }

    private AdapterView.OnItemClickListener setPlacesOnClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mChosenPlace = (Place) parent.getItemAtPosition(position);
            }
        };
    }


    private void loadSettings() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString("filter_prefs", "");
        FilterPrefs filterPrefs = gson.fromJson(json, FilterPrefs.class);
        if (filterPrefs != null) {
            int radio = filterPrefs.getGender().equalsIgnoreCase("Female") ? R.id.radio_female : R.id.radio_male;
            mRadioGroup.check(radio);

            mChosenPlace = filterPrefs.getPlace();

            if (mChosenPlace != null) {
                mPlaceAutoComplete.setText(mChosenPlace.getDescription());
            }

            int start = (int) mRangeBar.getTickStart();

            mRangeBar.setLeft(filterPrefs.getAgeMin() + start);
            mRangeBar.setRight(filterPrefs.getAgeMax() + start);
        }

    }


    private void saveSettings() {

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();


        String gender = mRadioGroup.getCheckedRadioButtonId() == R.id.radio_male ? "Male" : "Female";
        Place place = mChosenPlace;
        int tickStart = (int) mRangeBar.getTickStart();


        Integer ageMax = mRangeBar.getRightIndex() + tickStart;
        Integer ageMin = mRangeBar.getLeftIndex() + tickStart;
        Boolean isOnline = false;
        Boolean hasPicture = true;
        ArrayList<String> languages = new ArrayList<>();


        FilterPrefs prefs = new FilterPrefs();
        prefs.setGender(gender);
        prefs.setPlace(place);
        prefs.setAgeMax(ageMax);
        prefs.setAgeMin(ageMin);
        prefs.setIsOnline(isOnline);
        prefs.setHasPicture(hasPicture);
        prefs.setLanguages(languages);

        Gson gson = new Gson();
        String json = gson.toJson(prefs);
        prefsEditor.putString("filter_prefs", json);
        prefsEditor.commit();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mChosenPlace != null && !mChosenPlace.getDescription().equals(s.toString())) {
            mChosenPlace = null;
        }
    }


}
