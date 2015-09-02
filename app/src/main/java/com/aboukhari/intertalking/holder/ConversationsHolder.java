package com.aboukhari.intertalking.holder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Constants;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.model.Conversation;
import com.aboukhari.intertalking.model.User;
import com.firebase.client.Query;

/**
 * Created by aboukhari on 31/07/2015.
 */
public class ConversationsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Query ref;

    private final TextView mDisplayNameTextView;
    private final TextView mDateTextView;
    private final TextView mMessageTextView;
    private final ImageView mImageView;
    private final FireBaseManager fireBaseManager;
    private Conversation mConversation;
    private View view;
    private Context context;

    public ConversationsHolder(Context context, View itemView, Query ref, FireBaseManager fireBaseManager) {
        super(itemView);
        this.ref = ref;
        this.context = context;
        this.view = itemView;
        this.fireBaseManager = fireBaseManager;

        mDisplayNameTextView = (TextView) itemView.findViewById(R.id.tv_display_name);
        mDateTextView = (TextView) itemView.findViewById(R.id.tv_date);
        mMessageTextView = (TextView) itemView.findViewById(R.id.tv_message);
        mImageView = ((ImageView) itemView.findViewById(R.id.iv_profile));

        itemView.setOnClickListener(this);
    }

    public void bindConversation(final Conversation conversation, User friend) {
        final Long count = FireBaseManager.unreadMap.get(conversation.getRoomName()) == null ? 0L : FireBaseManager.unreadMap.get(conversation.getRoomName());
        mConversation = conversation;
        Log.d("natija read", " count = " + count);
        Log.d("natija read", " unreadMap = " + FireBaseManager.unreadMap);

        if (count > 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.md_grey_300));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        mDisplayNameTextView.setText(friend.getDisplayName());
        mDateTextView.setText(Constants.MESSAGE_DATE_FORMAT.format(conversation.getLastMessageDate()));
        mMessageTextView.setText(conversation.getLastMessage());
        Utils.loadImage(context, friend.getImageUrl(), mImageView);
    }

    @Override
    public void onClick(View v) {
        fireBaseManager.openRoom(mConversation.getRoomName());
    }

}
