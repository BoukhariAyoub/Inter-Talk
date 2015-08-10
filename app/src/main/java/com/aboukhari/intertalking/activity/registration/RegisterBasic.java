package com.aboukhari.intertalking.activity.registration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.Toast;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.CircularImageView;
import com.soundcloud.android.crop.Crop;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
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


        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("natija crop", "resultCode " + resultCode);
        Log.d("natija crop", "requestCode " + requestCode);

        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(data.getData());
            Log.d("natija crop", "begin crop " + requestCode);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
            Log.d("natija crop", "handle crop " + requestCode);
        }

    /*    if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Log.e("natija", "data = null");
                return;
            }
            try {


              //  Crop.of(inputUri, outputUri).asSquare().start(activity)


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
        }*/
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        com.aboukhari.intertalking.Utils.Crop.of(source, destination).asSquare().start(this);

    }

    private void handleCrop(int resultCode, Intent result)  {
        Log.d("natija crop","handle result code " + resultCode);
        Log.d("natija crop","handle result " + result);

        if (resultCode == getActivity().RESULT_OK) {
          //  mAvatarImageView.setImageURI(Crop.getOutput(result));
            try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(Crop.getOutput(result));
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
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = (new Intent("android.intent.action.GET_CONTENT")).setType("image/*");
        startActivityForResult(intent, Crop.REQUEST_PICK);

        Log.d("natija crop","pick image");

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        mBirthDateEditText.setText(date);
    }
}
