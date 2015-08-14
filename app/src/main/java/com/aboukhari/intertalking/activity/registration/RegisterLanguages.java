package com.aboukhari.intertalking.activity.registration;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.Utils.Variables;
import com.aboukhari.intertalking.adapter.CitiesAutoCompleteAdapter;
import com.aboukhari.intertalking.adapter.LanguagesRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.aboukhari.intertalking.model.Language;
import com.aboukhari.intertalking.model.Place;
import com.aboukhari.intertalking.retrofit.RestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by aboukhari on 06/08/2015.
 */
public class RegisterLanguages extends Fragment {

    private AutoCompleteTextView mPlaceAutoComplete, mLanguagesAutoComplete;
    LanguagesRecyclerAdapter mRecyclerAdapter;

    RecyclerView mRecyclerView;
    ArrayList<Language> mLanguagesList;
    CitiesAutoCompleteAdapter mCityAdapter;
    ArrayAdapter<Language> mLanguageAdapter;
    Place mChosenPlace;
    ArrayList<Language> mChosenLanguages = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_languages, container, false);

        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();



        mPlaceAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_city);
        mLanguagesAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.auto_languages_known);

        mCityAdapter = new CitiesAutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, Locale.getDefault().getLanguage());
        mPlaceAutoComplete.setThreshold(2);
        mPlaceAutoComplete.setAdapter(mCityAdapter);
        mPlaceAutoComplete.setOnItemClickListener(setPlacesOnClickListener());

        Language[] lang = getAllLanguages();
        mLanguageAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, lang);
        mLanguagesAutoComplete.setAdapter(mLanguageAdapter);
        mLanguagesAutoComplete.setOnItemClickListener(setLanguagesOnClickListener());


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewKnown);
        setupRecyclerView();


        return view;
    }


    private Language[] getAllLanguages() {
        mLanguagesList = jsonToLanguages(Utils.loadJSONFromAsset(getActivity(), "languages.json"));
        return mLanguagesList.toArray(new Language[mLanguagesList.size()]);
    }


    private ArrayList<Language> getSelectedLanguages() {
        ArrayList<Language> languages = new ArrayList<>();
        return languages;
    }


    private ArrayList<Language> jsonToLanguages(String json) {
       ArrayList<Language> languages = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++)
            {
                String iso = jsonArray.getString(i);

                Language language = new Language(iso);

                languages.add(language);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return languages;
    }


    private void setupRecyclerView() {
        mRecyclerView.setHasFixedSize(false);
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new OvershootInLeftAnimator());
        mRecyclerAdapter = new LanguagesRecyclerAdapter(this.getActivity(),getSelectedLanguages(),mLanguageAdapter);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }


    private void updatePlace(final Place place) {
        RestClient.get(RestClient.GOOGLE_MAPS_ENDPOINT).getPlaceDetails(place.getId(), "en", Variables.GOOGLE_API_KEY, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement json, Response response) {
                JsonObject placeJson = json.getAsJsonObject().get("result").getAsJsonObject();

                place.setDescription(placeJson.get("formatted_address").getAsString());
                place.setUrl(placeJson.get("url").getAsString());


                JsonObject location = placeJson.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
                place.setLatitude(location.get("lat").getAsDouble());
                place.setLongitude(location.get("lng").getAsDouble());


                JsonArray components = placeJson.get("address_components").getAsJsonArray();


                for (JsonElement comp : components) {
                    JsonObject jsonComp = comp.getAsJsonObject();
                    JsonArray types = jsonComp.get("types").getAsJsonArray();
                    String shortName = jsonComp.get("short_name").getAsString();
                    String longName = jsonComp.get("long_name").getAsString();

                    String type = types.get(0).getAsString();

                    switch (type) {
                        case "locality":
                            place.setCity(longName);
                            break;
                        case "administrative_area_level_1":
                            place.setRegion(shortName);
                            break;
                        case "country":
                            place.setCountry(shortName);
                            break;
                    }
                }
                mChosenPlace = place;
            }


            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private AdapterView.OnItemClickListener setPlacesOnClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Place oldPlace = (Place) parent.getItemAtPosition(position);
                updatePlace(oldPlace);

            }
        };
    }

    private AdapterView.OnItemClickListener setLanguagesOnClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Language language = (Language) parent.getItemAtPosition(position);
                mRecyclerAdapter.addItem(language);
                mLanguagesAutoComplete.setText("");
            }
        };
    }


}
