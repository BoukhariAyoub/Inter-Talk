package com.aboukhari.intertalking.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.model.User;
import com.cloudinary.Cloudinary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Created by aboukhari on 20/08/2015.
 */
public class UploadImage extends AsyncTask<Bitmap, Integer, String> {

    Context context;
    User user;

    public UploadImage(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        try {

            Log.d("natija", "params = " + params.toString());
            Bitmap bitmap = params[0];

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

            Map map = Utils.getCloudinary().uploader().upload(bs, Cloudinary.asMap("public_id", user.getUid()));

            return map.get("url").toString();


        } catch (Exception e) {
            Log.e("natija cloud", e.getMessage());

        }
        return null;

    }

    @Override
    protected void onPostExecute(String imageUrl) {
        super.onPostExecute(imageUrl);
        if (imageUrl != null) {
            FireBaseManager.getInstance(context).addImageToUser(user, imageUrl);
        }
    }
}
