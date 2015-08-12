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
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.activity.SpringIndicator;
import com.aboukhari.intertalking.model.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.soundcloud.android.crop.Crop;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import info.hoang8f.android.segmented.SegmentedGroup;

public class RegisterBasic extends Fragment implements View.OnTouchListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {


    CircularImageView mAvatarImageView;
    EditText mUserNameEditText, mEmailEditText, mPasswordEditText, mBirthDateEditText;
    SegmentedGroup mSegmentedGroup;
    HashMap<String, Integer> mGenderMap = new HashMap<>();
    User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_register_basic, container, false);


        mAvatarImageView = (CircularImageView) v.findViewById(R.id.iv_avatar);
        mUserNameEditText = (EditText) v.findViewById(R.id.et_name);
        mEmailEditText = (EditText) v.findViewById(R.id.et_email);
        mPasswordEditText = (EditText) v.findViewById(R.id.et_password);
        mBirthDateEditText = (EditText) v.findViewById(R.id.et_birthday);
        mSegmentedGroup = (SegmentedGroup) v.findViewById(R.id.segmented2);
        mUser = ((SpringIndicator) getActivity()).getmUser();

        mGenderMap.put("female", R.id.radio_female);
        mGenderMap.put("male", R.id.radio_male);

        mPasswordEditText.setOnTouchListener(this);
        mAvatarImageView.setOnClickListener(this);
        mUserNameEditText.setOnClickListener(this);
        mEmailEditText.setOnClickListener(this);
        mPasswordEditText.setOnClickListener(this);
        mBirthDateEditText.setOnClickListener(this);


        if (mUser != null) {
            populateUser(mUser);
            Log.d("natija user", mUser.toString());
        }


        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity(), this);

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == getActivity().RESULT_OK) {
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
                    mPasswordEditText.setTransformationMethod(null);
                    return true;
                }
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (mPasswordEditText.getRight() - mPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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
        Log.d("natija crop", "pick image");

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Date d = Utils.stringToDate(date);
        mBirthDateEditText.setText(date);
    }


    private void populateUser(User user) {
        mUserNameEditText.setText(user.getDisplayName());
        mEmailEditText.setText(user.getEmail());
        int radio = mGenderMap.get(user.getGender());
        mSegmentedGroup.check(radio);
        mBirthDateEditText.setText(Utils.dateToString(user.getBirthday()));


        ImageLoader imageLoader = ImageLoader.getInstance();


        imageLoader.loadImage(user.getImageUrl(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.d("natija image", loadedImage.getByteCount() + "");
                mAvatarImageView.setImageBitmap(loadedImage);
                mAvatarImageView.addShadow();
                mAvatarImageView.setBorderColor(getResources().getColor(R.color.md_grey_300));
                mAvatarImageView.setBorderWidth(1);
                mAvatarImageView.setSelectorStrokeColor(getResources().getColor(R.color.white));
                mAvatarImageView.setSelectorStrokeWidth(1);
                mAvatarImageView.addShadow();
            }
        });


    }
}
