package com.aboukhari.intertalking.activity.registration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.activity.SpringIndicator;
import com.aboukhari.intertalking.adapter.CitiesAutoCompleteAdapter;
import com.aboukhari.intertalking.adapter.LanguagesRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.aboukhari.intertalking.model.Language;
import com.aboukhari.intertalking.model.Place;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

/**
 * Created by aboukhari on 06/08/2015.
 */
public class RegisterLanguages extends Fragment implements View.OnClickListener {

    private AutoCompleteTextView mPlaceAutoComplete, mLanguagesKnownAutoComplete, mLanguagesWantedAutoComplete;

    Button btnRegister;
    RecyclerView mRecyclerViewKnown, mRecyclerViewWanted;
    ArrayList<Language> mLanguagesList;
    CitiesAutoCompleteAdapter mCityAdapter;
    Place mChosenPlace;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_languages, container, false);

        btnRegister = (Button) view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);


        mPlaceAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_city);


        mCityAdapter = new CitiesAutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, Locale.getDefault().getLanguage());
        mPlaceAutoComplete.setThreshold(2);
        mPlaceAutoComplete.setAdapter(mCityAdapter);
        mPlaceAutoComplete.setOnItemClickListener(setPlacesOnClickListener());


        mLanguagesKnownAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_languages_known);
        mLanguagesWantedAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_languages_wanted);

        mRecyclerViewKnown = (RecyclerView) view.findViewById(R.id.recyclerViewKnown);
        mRecyclerViewWanted = (RecyclerView) view.findViewById(R.id.recyclerViewWanted);

        setUpLanguageViews(mLanguagesKnownAutoComplete, mRecyclerViewKnown, true);
        setUpLanguageViews(mLanguagesWantedAutoComplete, mRecyclerViewWanted, false);


        return view;
    }

    private void setUpLanguageViews(AutoCompleteTextView autoCompleteTextView, RecyclerView recyclerView, boolean known) {
        ArrayAdapter<Language> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getAllLanguages());
        autoCompleteTextView.setAdapter(arrayAdapter);
        LanguagesRecyclerAdapter recyclerAdapter = new LanguagesRecyclerAdapter(getActivity(), new ArrayList<Language>(), arrayAdapter, known);
        autoCompleteTextView.setOnItemClickListener(setLanguagesOnClickListener(recyclerAdapter, autoCompleteTextView));

        recyclerView.setHasFixedSize(false);
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());
        recyclerView.setAdapter(recyclerAdapter);
    }


    private Language[] getAllLanguages() {
        mLanguagesList = jsonToLanguages(Utils.loadJSONFromAsset(getActivity(), "languages.json"));
        return mLanguagesList.toArray(new Language[mLanguagesList.size()]);
    }


    private ArrayList<Language> getSelectedLanguages(RecyclerView recyclerView) {
        LanguagesRecyclerAdapter adapter = (LanguagesRecyclerAdapter) recyclerView.getAdapter();
        return adapter.getLanguages();
    }


    private ArrayList<Language> jsonToLanguages(String json) {
        ArrayList<Language> languages = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                String iso = jsonArray.getString(i);

                Language language = new Language(iso);

                languages.add(language);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return languages;
    }


    private AdapterView.OnItemClickListener setLanguagesOnClickListener(final LanguagesRecyclerAdapter adapter, final AutoCompleteTextView autoCompleteTextView) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Language language = (Language) parent.getItemAtPosition(position);
                adapter.addItem(language);
                autoCompleteTextView.setText("");
            }
        };
    }

    private AdapterView.OnItemClickListener setPlacesOnClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mChosenPlace = (Place) parent.getItemAtPosition(position);
            }
        };
    }

    public ArrayList<Language> getKnownLanguages(){
        ArrayList<Language> knownLanguages = getSelectedLanguages(mRecyclerViewKnown);
        return knownLanguages;
    }

    public ArrayList<Language> getWantedLanguages(){
        ArrayList<Language> wantedLanguages = getSelectedLanguages(mRecyclerViewWanted);
        return wantedLanguages;
    }

    public String getPlaceId(){
        return mChosenPlace.getId();
    }



    @Override
    public void onClick(View v) {
        ((SpringIndicator) getActivity()).register();
    }




}
