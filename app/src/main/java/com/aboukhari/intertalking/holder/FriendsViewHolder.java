package com.aboukhari.intertalking.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.model.User;

/**
 * Created by ayoub.boukhari on 03/12/2015.
 */
public class FriendsViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {


    TextView mDisplayNameTextView;
    ImageView mAvatarImageView;
    private final FireBaseManager fireBaseManager;
    private View view;
    private Context context;

    public FriendsViewHolder(Context context, View itemView, FireBaseManager fireBaseManager) {
        super(itemView);
        this.context = context;
        this.view = itemView;
        this.fireBaseManager = fireBaseManager;

        mDisplayNameTextView = (TextView) itemView.findViewById(R.id.tv_display_name);
        mAvatarImageView = ((ImageView) view.findViewById(R.id.iv_profile));


        itemView.setOnClickListener(this);
    }

    public void bindConversation(User friend) {
        mDisplayNameTextView.setText(friend.getDisplayName());
        Utils.loadImage(friend.getImageUrl(), mAvatarImageView);
    }

    @Override
    public void onClick(View v) {

    }
}
