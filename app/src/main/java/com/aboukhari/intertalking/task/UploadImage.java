package com.aboukhari.intertalking.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.cloudinary.Cloudinary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Created by aboukhari on 20/08/2015.
 */
public class UploadImage extends AsyncTask<Object, Integer, String[]> {

    Context context;

    public UploadImage(Context context) {
        this.context = context;
    }

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
            return s;


        } catch (Exception e) {
            Log.e("natija cloud", e.getMessage());

        }
        return null;

    }

    @Override
    protected void onPostExecute(String[] s) {
        super.onPostExecute(s);
        if(s!=null) {
            String uid = s[0];
            String imageUrl = s[1];
            FireBaseManager.getInstance(context).addImageToUser(uid, imageUrl);
        }
    }
}
