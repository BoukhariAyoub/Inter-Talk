package com.aboukhari.intertalking.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.model.Friend;
import com.firebase.client.Query;

import java.io.File;

/**
 * Created by aboukhari on 16/07/2015.
 */
public class FriendListAdapter extends FirebaseListAdapter<Friend> {

    /**
     * @param ref      The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                 combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param layout   This is the layout used to represent a single list item. You will be responsible for populating an
     *                 instance of the corresponding view with the data from an instance of modelClass.
     * @param activity The activity containing the ListView
     */
    public FriendListAdapter(Query ref, int layout, Activity activity) {
        super(ref, Friend.class, layout, activity);
    }

    @Override
    protected void populateView(View view, Friend friend) {
        ImageView imageView = ((ImageView) view.findViewById(R.id.iv_profile));
        setImage(friend,imageView);
        ((TextView) view.findViewById(R.id.tv_display_name)).setText(friend.getDisplayName());
    }

    private void setImage(Friend friend,ImageView imageView){

        String dirPath = imageView.getContext().getFilesDir().getAbsolutePath() + File.separator + "pic-profile";

        File imgFile = new File(Environment.getExternalStorageDirectory() + "/" + dirPath + "/" + friend.getuId() + ".jpg");

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }
}
