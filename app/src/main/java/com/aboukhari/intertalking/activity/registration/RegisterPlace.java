package com.aboukhari.intertalking.activity.registration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.adapter.CitiesAutoCompleteAdapter;
import com.aboukhari.intertalking.model.Place;

import java.util.Locale;

/**
 * Created by aboukhari on 20/08/2015.
 */
public class RegisterPlace extends Fragment {

    CitiesAutoCompleteAdapter mCityAdapter;
    Place mChosenPlace;
    private AutoCompleteTextView mPlaceAutoComplete;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration_place, container, false);


        mPlaceAutoComplete = (AutoCompleteTextView) v.findViewById(R.id.auto_city);
        mCityAdapter = new CitiesAutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, Locale.getDefault().getLanguage());
        mPlaceAutoComplete.setThreshold(2);
        mPlaceAutoComplete.setAdapter(mCityAdapter);
        mPlaceAutoComplete.setOnItemClickListener(setPlacesOnClickListener());


        return v;
    }

    private AdapterView.OnItemClickListener setPlacesOnClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mChosenPlace = (Place) parent.getItemAtPosition(position);
            }
        };
    }

    public String getPlaceId() {
        return mChosenPlace.getId();
    }


}
