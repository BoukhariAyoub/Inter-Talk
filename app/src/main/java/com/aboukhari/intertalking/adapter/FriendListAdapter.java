package com.aboukhari.intertalking.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.model.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

/**
 * Created by aboukhari on 16/07/2015.
 */
public class FriendListAdapter extends FirebaseListAdapter<User> {

    /**
     * @param ref      The Firebase location to watch for data changes. Can also be a slice of a location, using some
     * combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param layout   This is the layout used to represent a single list item. You will be responsible for populating an
     * instance of the corresponding view with the data from an instance of modelClass.
     * @param activity The activity containing the ListView
     */

    Firebase ref;
    Activity activity;

    public FriendListAdapter(Query ref,Activity activity) {
        super(ref, User.class, R.layout.item_friend_list, activity);
        this.activity = activity;
        this.ref = ref.getRef();
    }


    @Override
    protected void populateView(final View view,ViewGroup viewGroup, final User friend) {

        ref.getRoot().child("users").child(friend.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String name = dataSnapshot.child("displayName").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    TextView displayNameTextView = (TextView) view.findViewById(R.id.tv_display_name);
                    displayNameTextView.setText(name);
                    ImageView imageView = ((ImageView) view.findViewById(R.id.iv_profile));
                    Utils.loadImage(activity, imageUrl, imageView);
                } else {
                    ref.getRoot().child("users").child(ref.getAuth().getUid()).child("friends").child(friend.getUid()).removeValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("natija data", "cancel " + firebaseError);
            }
        });
    }


}
