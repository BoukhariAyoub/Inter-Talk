package com.aboukhari.intertalking.activity.registration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.adapter.LanguagesRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.aboukhari.intertalking.model.Language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by aboukhari on 06/08/2015.
 */
public class RegisterLanguages extends Fragment {

    private AutoCompleteTextView mCountryAutoComplete, mCityAutoComplete, mLanguagesAutoComplete;
    RecyclerView rv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_languages, container, false);


        mCountryAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_country);
        mCityAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_city);
        mLanguagesAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_languages);

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getAllCountries());
        mCountryAutoComplete.setAdapter(countryAdapter);

        rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(), 3);
        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(new FadeInAnimator());

        LanguagesRecyclerAdapter recyclerAdapter = new LanguagesRecyclerAdapter(getSelectedLanguages());
        rv.setAdapter(recyclerAdapter);


        return view;
    }


    private String[] getAllLanguages() {
        String[] strings = new String[]{"france", "england"};

        return strings;
    }

    private String[] getAllCountries() {
        ArrayList<Language> c = jsonToList(loadJSONFromAsset());
        ArrayList<String> countries = new ArrayList<>();
        for (Language language : c) {
            countries.add(language.getName());
        }

        return  countries.toArray(new String[countries.size()]);
    }

    private ArrayList<Language> getSelectedLanguages() {

        ArrayList<Language> languages = new ArrayList<>();
        languages.add(new Language("fr", "Francais"));
        languages.add(new Language("en", "English"));
        languages.add(new Language("fr", "Francais"));
        languages.add(new Language("en", "English"));
        languages.add(new Language("fr", "Francais"));
        languages.add(new Language("en", "English"));
        languages.add(new Language("fr", "Francais"));
        languages.add(new Language("en", "English"));
        languages.add(new Language("fr", "Francais"));
        languages.add(new Language("en", "English"));
        languages.add(new Language("fr", "Francais"));
        languages.add(new Language("en", "English"));
        return languages;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("countries.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;


    }

    private ArrayList<Language> jsonToList(String json) {
        ArrayList<Language> languages = new ArrayList<>();
        Log.d("natija json", json);
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0, size = jsonArray.length(); i < size; i++) {
                JSONObject objectInArray = jsonArray.getJSONObject(i);
                String iso = objectInArray.getString("iso");
                String fullName = objectInArray.getString("fullName");

                Language language = new Language(iso, fullName);
                languages.add(language);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return languages;
    }


}
