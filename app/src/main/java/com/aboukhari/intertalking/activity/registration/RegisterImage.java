package com.aboukhari.intertalking.activity.registration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.CircularImageView;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by aboukhari on 20/08/2015.
 */
public class RegisterImage extends Fragment implements View.OnClickListener {

    CircularImageView mAvatarImageView;
    Bitmap bitmap;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_registration_image, container, false);
        mAvatarImageView = (CircularImageView) v.findViewById(R.id.iv_avatar);
        mAvatarImageView.setOnClickListener(this);


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
    }


    private void pickImage() {
        Intent intent = (new Intent("android.intent.action.GET_CONTENT")).setType("image/*");
        startActivityForResult(intent, Crop.REQUEST_PICK);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }


}
