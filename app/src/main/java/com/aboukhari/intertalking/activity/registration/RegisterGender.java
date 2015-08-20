package com.aboukhari.intertalking.activity.registration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aboukhari.intertalking.R;

import java.util.HashMap;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by aboukhari on 20/08/2015.
 */
public class RegisterGender extends Fragment {


    SegmentedGroup mSegmentedGroup;
    HashMap<String, Integer> mGenderMap = new HashMap<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_registration_gender, container, false);


        mSegmentedGroup = (SegmentedGroup) v.findViewById(R.id.segmented2);

        mGenderMap.put("female", R.id.radio_female);
        mGenderMap.put("male", R.id.radio_male);


        return v;
    }

    public String getGender() {
        return mGenderMap.get("male").equals(mSegmentedGroup.getCheckedRadioButtonId()) ? "male" : "female";
    }
}
