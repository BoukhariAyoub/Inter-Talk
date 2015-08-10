package com.aboukhari.intertalking.Utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.soundcloud.android.crop.CropImageActivity;

/**
 * Created by aboukhari on 10/08/2015.
 */
public class Crop {
    public static final int REQUEST_CROP = 6709;
    public static final int REQUEST_PICK = 9162;
    public static final int RESULT_ERROR = 404;
    private Intent cropIntent = new Intent();

    public static Crop of(Uri source, Uri destination) {
        return new Crop(source, destination);
    }

    private Crop(Uri source, Uri destination) {
        this.cropIntent.setData(source);
        this.cropIntent.putExtra("output", destination);
    }

    public Crop withAspect(int x, int y) {
        this.cropIntent.putExtra("aspect_x", x);
        this.cropIntent.putExtra("aspect_y", y);
        return this;
    }

    public Crop asSquare() {
        this.cropIntent.putExtra("aspect_x", 1);
        this.cropIntent.putExtra("aspect_y", 1);
        return this;
    }

    public Crop withMaxSize(int width, int height) {
        this.cropIntent.putExtra("max_x", width);
        this.cropIntent.putExtra("max_y", height);
        return this;
    }

    public void start(Activity activity) {
        this.start(activity, 6709);
    }

    public void start(Activity activity, int requestCode) {
        activity.startActivityForResult(this.getIntent(activity), requestCode);
    }

    public void start(Context context, Fragment fragment) {
        this.start(context, (Fragment)fragment, 6709);
    }

    public void start(Context context, android.support.v4.app.Fragment fragment) {
        this.start(context, (android.support.v4.app.Fragment)fragment, 6709);
    }

    public void start(Context context, Fragment fragment, int requestCode) {
        fragment.startActivityForResult(this.getIntent(context), requestCode);
    }

    public void start(Context context, android.support.v4.app.Fragment fragment, int requestCode) {
        fragment.startActivityForResult(this.getIntent(context), requestCode);
    }

    public Intent getIntent(Context context) {
        this.cropIntent.setClass(context, CropImageActivity.class);
        return this.cropIntent;
    }

    public static Uri getOutput(Intent result) {
        return (Uri)result.getParcelableExtra("output");
    }

    public static Throwable getError(Intent result) {
        return (Throwable)result.getSerializableExtra("error");
    }

    public static void pickImage(Activity activity) {
        pickImage(activity, 9162);
    }

    public static void pickImage(Activity activity, int requestCode) {
        Intent intent = (new Intent("android.intent.action.GET_CONTENT")).setType("image/*");

        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException var4) {
            Toast.makeText(activity, com.soundcloud.android.crop.R.string.crop__pick_error, 0).show();
        }

    }

    interface Extra {
        String ASPECT_X = "aspect_x";
        String ASPECT_Y = "aspect_y";
        String MAX_X = "max_x";
        String MAX_Y = "max_y";
        String ERROR = "error";
    }
}
