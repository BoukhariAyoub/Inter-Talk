package com.aboukhari.intertalking.holder;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.activity.profile.ProfileView;
import com.aboukhari.intertalking.model.Place;
import com.aboukhari.intertalking.model.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by aboukhari on 17/08/2015.
 */
public class FindUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    User mUser;
    private final TextView mDisplayName, mAge, mPlace, mStatus;
    ImageView mAvatarImageView, mFlagImageView;
    Context mContext;


    public FindUserViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mDisplayName = (TextView) itemView.findViewById(R.id.tv_display_name);
        mPlace = (TextView) itemView.findViewById(R.id.tv_place);
        mStatus = (TextView) itemView.findViewById(R.id.tv_status);
        //  mPresenceStatus = (TextView) itemView.findViewById(R.id.tv_presence_status);
        mAge = (TextView) itemView.findViewById(R.id.tv_age);
        mAvatarImageView = (ImageView) itemView.findViewById(R.id.iv_avatar);
        mFlagImageView = (ImageView) itemView.findViewById(R.id.iv_flag);
        mAvatarImageView.setOnClickListener(this);

    }

    public FindUserViewHolder bindUser(User user) {
        mUser = user;
        mDisplayName.setText(user.getDisplayName());
        mAge.setText(user.getAge() + " years");
        Firebase ref = new Firebase(mContext.getString(R.string.firebase_url));

        Log.d("natija", user.toString());
        if(user.getPlace()!=null){
            ref.getRoot().child("places").child(user.getPlace()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("natija place", dataSnapshot.toString());
                    Place place = dataSnapshot.getValue(Place.class);
                    mPlace.setText(place.getDescription());
                    Utils.loadImage(Utils.getFlagUrl(place.getCountry()), mFlagImageView);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }


        Utils.loadImage(user.getImageUrl(), mAvatarImageView);
        int color = user.getGender().equalsIgnoreCase("male") ? R.color.md_teal_A700 : R.color.md_red_A200;

        return this;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, ProfileView.class);
        intent.putExtra("user", mUser);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View sharedView = v;
            String transitionName = "imageTransition";

            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, sharedView, transitionName);
            mContext.startActivity(intent, transitionActivityOptions.toBundle());
        } else {
            mContext.startActivity(intent);
        }


    }
}
