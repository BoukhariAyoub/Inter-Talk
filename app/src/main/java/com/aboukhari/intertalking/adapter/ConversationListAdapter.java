package com.aboukhari.intertalking.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.model.Conversation;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by aboukhari on 23/07/2015.
 */
public class ConversationListAdapter extends FirebaseListAdapter<Conversation> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");


    /**
     * @param ref        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                   combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param layout     This is the layout used to represent a single list item. You will be responsible for populating an
     *                   instance of the corresponding view with the data from an instance of modelClass.
     * @param activity   The activity containing the ListView
     */

    Query ref;
    public ConversationListAdapter(Query ref, int layout, Activity activity) {
        super(ref, Conversation.class, layout, activity);
        this.ref = ref;
}

    @Override
    protected void populateView(final View view,final Conversation conversation) {

        Log.d("natija","populateView " + conversation);

        ImageView imageView = ((ImageView) view.findViewById(R.id.iv_profile));
        setImage(conversation, imageView);
        ref.getRef().getRoot().child("users").child(conversation.extractFriendUid(ref.getRef())).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView) view.findViewById(R.id.tv_display_name)).setText(dataSnapshot.getValue().toString());
                ((TextView) view.findViewById(R.id.tv_date)).setText(DATE_FORMAT.format(conversation.getLastMessageDate()));
                ((TextView) view.findViewById(R.id.tv_message)).setText(conversation.getLastMessage());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setImage(Conversation conversation,ImageView imageView){

        String dirPath = imageView.getContext().getFilesDir().getAbsolutePath() + File.separator + "pic-profile";

        File imgFile = new File(Environment.getExternalStorageDirectory() + "/" + dirPath + "/" + conversation.extractFriendUid(ref.getRef()) + ".jpg");

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }




}
