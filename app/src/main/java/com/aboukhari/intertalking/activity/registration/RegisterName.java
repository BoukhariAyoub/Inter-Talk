package com.aboukhari.intertalking.activity.registration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aboukhari.intertalking.R;

/**
 * Created by aboukhari on 20/08/2015.
 */
public class RegisterName extends Fragment {

    EditText mUserNameEditText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_registration_name, container, false);

        mUserNameEditText = (EditText) v.findViewById(R.id.et_name);

        return v;
    }

    public String getName(){
        return mUserNameEditText.getText().toString();
    }

}
