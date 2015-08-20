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
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.Utils.Variables;
import com.aboukhari.intertalking.activity.SpringIndicator;
import com.aboukhari.intertalking.adapter.CitiesAutoCompleteAdapter;
import com.aboukhari.intertalking.adapter.LanguagesRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.aboukhari.intertalking.model.Language;
import com.aboukhari.intertalking.model.Place;
import com.aboukhari.intertalking.model.User;
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

        setUpLanguageViews(mLanguagesKnownAutoComplete, mRecyclerViewKnown);
        setUpLanguageViews(mLanguagesWantedAutoComplete, mRecyclerViewWanted);


        return view;
    }

    private void setUpLanguageViews(AutoCompleteTextView autoCompleteTextView, RecyclerView recyclerView) {
        ArrayAdapter<Language> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getAllLanguages());
        autoCompleteTextView.setAdapter(arrayAdapter);
        LanguagesRecyclerAdapter recyclerAdapter = new LanguagesRecyclerAdapter(getActivity(), new ArrayList<Language>(), arrayAdapter);
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

    public void register(final User user) {

        registerLanguages(user);
        registerPlace(user);

    }

    private Place jSonToPlace(JsonElement json) {
        Place place = new Place();
        JsonObject placeJson = json.getAsJsonObject().get("result").getAsJsonObject();

        place.setId(placeJson.get("place_id").getAsString());
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
        return place;
    }


    @Override
    public void onClick(View v) {
        ((SpringIndicator) getActivity()).register();
    }


    private void registerLanguages(User user) {
        ArrayList<Language> knownLanguages = getSelectedLanguages(mRecyclerViewKnown);
        ArrayList<Language> wantedLanguages = getSelectedLanguages(mRecyclerViewWanted);

        FireBaseManager fireBaseManager = new FireBaseManager(getActivity());

        for (Language language : knownLanguages) {
            String languageType = "knownLanguages";
            fireBaseManager.addLanguageToUser(user.getUid(), languageType, language);
        }

        for (Language language : wantedLanguages) {
            String languageType = "wantedLanguages";
            fireBaseManager.addLanguageToUser(user.getUid(), languageType, language);
        }

    }

    private void registerPlace(final User user) {
        RestClient.get(RestClient.GOOGLE_MAPS_ENDPOINT).getPlaceDetails(mChosenPlace.getId(), "en", Variables.GOOGLE_API_KEY, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement json, Response response) {
                /*Set Place To User*/
                mChosenPlace = jSonToPlace(json);
                user.setPlaceId(mChosenPlace.getId());
                user.setPlaceName(mChosenPlace.getDescription());
                FireBaseManager.getInstance(getActivity()).addPlace(mChosenPlace);
                FireBaseManager.getInstance(getActivity()).addUserToFireBase(user);


            }


            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
