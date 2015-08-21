package com.aboukhari.intertalking.activity.registration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by aboukhari on 21/08/2015.
 */
public class RegisterFusion extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    EditText mUserNameEditText;
    SegmentedGroup mSegmentedGroup;
    HashMap<String, Integer> mGenderMap = new HashMap<>();
    EditText mBirthDateEditText;
    Date selectedBirthdate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_register_fusion, container, false);
        mUserNameEditText = (EditText) v.findViewById(R.id.et_name);
        mSegmentedGroup = (SegmentedGroup) v.findViewById(R.id.segmented2);
        mGenderMap.put("female", R.id.radio_female);
        mGenderMap.put("male", R.id.radio_male);
        mBirthDateEditText = (EditText) v.findViewById(R.id.et_birthday);
        mBirthDateEditText.setOnClickListener(this);

        return v;
    }

    public String getName() {
        return mUserNameEditText.getText().toString();
    }

    public String getGender() {
        return mGenderMap.get("male").equals(mSegmentedGroup.getCheckedRadioButtonId()) ? "male" : "female";
    }

    public Date getBirthdate() {
        return selectedBirthdate;
    }


    @Override
    public void onClick(View v) {
        if (v == mBirthDateEditText) {

            Calendar start = Calendar.getInstance();
            start.add(Calendar.YEAR,-15);
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    start.get(Calendar.YEAR),
                    start.get(Calendar.MONTH),
                    start.get(Calendar.DAY_OF_MONTH)
            );

            Calendar now = Calendar.getInstance();
            int startYear = now.get(Calendar.YEAR) - 99;
            int endYear = now.get(Calendar.YEAR) - 5;
            datePickerDialog.setYearRange(startYear,endYear);

            datePickerDialog.show(getActivity().getFragmentManager(), "Datepickerdialog");
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Date d = Utils.stringToDate(date);
        mBirthDateEditText.setText(date);
        selectedBirthdate = d;
    }

}
