package com.aboukhari.intertalking.activity.registration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.CircularImageView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class RegisterBasic extends Fragment implements View.OnTouchListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {


    CircularImageView mAvatarImageView;
    EditText mUserNameEditText, mEmailEditText, mPasswordEditText, mBirthDateEditText, mGenderEditText;
    static final int PICK_PHOTO_FOR_AVATAR = 20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_register_basic, container, false);
        mAvatarImageView = (CircularImageView) v.findViewById(R.id.iv_avatar);
        mUserNameEditText = (EditText) v.findViewById(R.id.et_name);
        mEmailEditText = (EditText) v.findViewById(R.id.et_email);
        mPasswordEditText = (EditText) v.findViewById(R.id.et_password);
        mBirthDateEditText = (EditText) v.findViewById(R.id.et_birthday);
       // mGenderEditText = (EditText) v.findViewById(R.id.et_gender);


        mPasswordEditText.setOnTouchListener(this);


        mAvatarImageView.setOnClickListener(this);
        mUserNameEditText.setOnClickListener(this);
        mEmailEditText.setOnClickListener(this);
        mPasswordEditText.setOnClickListener(this);
        mBirthDateEditText.setOnClickListener(this);
       // mGenderEditText.setOnClickListener(this);


        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Log.e("natija", "data = null");
                return;
            }
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                mAvatarImageView.setImageBitmap(bmp);
                mAvatarImageView.addShadow();
                mAvatarImageView.setBorderColor(getResources().getColor(R.color.md_grey_300));
                mAvatarImageView.setBorderWidth(1);
                mAvatarImageView.setSelectorStrokeColor(getResources().getColor(R.color.white));
                mAvatarImageView.setSelectorStrokeWidth(1);
                mAvatarImageView.addShadow();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }


    @Override
    public void onClick(View v) {
        if (v == mAvatarImageView) {
            pickImage();
        }
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
    public boolean onTouch(View v, MotionEvent event) {
        if (v == mPasswordEditText) {


            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (event.getRawX() >= (mPasswordEditText.getRight() - mPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // your action here
                    Log.d("natija", "eye touché");
                    mPasswordEditText.setTransformationMethod(null);
                    return true;
                }
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (mPasswordEditText.getRight() - mPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // your action here
                    Log.d("natija", "eye untouché");
                    mPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
                    return true;
                }
            }
        }
        return false;
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        mBirthDateEditText.setText(date);
    }
}
