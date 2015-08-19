package com.aboukhari.intertalking.activity.registration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.model.User;
import com.cloudinary.Cloudinary;
import com.soundcloud.android.crop.Crop;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import info.hoang8f.android.segmented.SegmentedGroup;

public class RegisterBasic extends Fragment implements View.OnTouchListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {


    CircularImageView mAvatarImageView;
    EditText mUserNameEditText, mEmailEditText, mPasswordEditText, mBirthDateEditText;
    SegmentedGroup mSegmentedGroup;
    HashMap<String, Integer> mGenderMap = new HashMap<>();
    InputStream mInputStream;
    Date selectedBirthdate;
    Bitmap bitmap;

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

        mGenderMap.put("female", R.id.radio_female);
        mGenderMap.put("male", R.id.radio_male);

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
                mInputStream = inputStream;
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                bitmap = bmp;

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
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Date d = Utils.stringToDate(date);
        mBirthDateEditText.setText(date);
        selectedBirthdate = d;
    }


    private void populateUser(User user) {
        mUserNameEditText.setText(user.getDisplayName());
        mEmailEditText.setText(user.getEmail());
        int radio = mGenderMap.get(user.getGender());
        mSegmentedGroup.check(radio);
        mBirthDateEditText.setText(Utils.dateToString(user.getBirthday()));
        Utils.setImage(getActivity(), user.getImageUrl(), mAvatarImageView);
    }


    public CircularImageView getmAvatarImageView() {
        return mAvatarImageView;
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    public User register(User user) {
        user.setDisplayName(mUserNameEditText.getText().toString());
        user.setEmail(mEmailEditText.getText().toString());
        String gender = mGenderMap.get("male").equals(mSegmentedGroup.getCheckedRadioButtonId()) ? "male" : "female";
        user.setGender(gender);
        user.setBirthday(selectedBirthdate);
        FireBaseManager.getInstance(getActivity()).addUserToFireBase(user);

        new UploadImage().execute(bitmap, user.getUid());

        Log.d("natija user", user.toString());

        return user;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }


    private class UploadImage extends AsyncTask<Object, Integer, String[]> {


        @Override
        protected String[] doInBackground(Object... params) {
            try {

                Log.d("natija", "params = " + params.toString());
                Bitmap bitmap = (Bitmap) params[0];
                String uid = params[1].toString();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                Map map = Utils.getCloudinary().uploader().upload(bs, Cloudinary.asMap("public_id", uid));
                String[] s = new String[2];
                s[0] = uid;
                s[1] = map.get("url").toString();
                return s ;


            } catch (Exception e) {
                Log.e("natija cloud", e.getMessage());

            }

            return null;

        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            String uid = s[0];
            String imageUrl = s[1];
            FireBaseManager.getInstance(getActivity()).addImageToUser(uid, imageUrl);
        }
    }


}
