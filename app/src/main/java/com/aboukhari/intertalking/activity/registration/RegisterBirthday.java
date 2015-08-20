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

/**
 * Created by aboukhari on 20/08/2015.
 */
public class RegisterBirthday extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    EditText mBirthDateEditText;
    Date selectedBirthdate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_registration_birthdate, container, false);

        mBirthDateEditText = (EditText) v.findViewById(R.id.et_birthday);
        mBirthDateEditText.setOnClickListener(this);


        return v;
    }

    public Date getBirthdate() {
        return selectedBirthdate;
    }

    @Override
    public void onClick(View v) {
        if (v == mBirthDateEditText) {

            Calendar now = Calendar.getInstance();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );

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
