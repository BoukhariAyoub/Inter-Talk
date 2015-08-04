package com.aboukhari.intertalking.holder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.model.Conversation;
import com.firebase.client.Query;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
    private Conversation mConversation;
    private View view;

    public ConversationsHolder(View itemView, Query ref, FireBaseManager fireBaseManager) {
        super(itemView);
        this.ref = ref;
        this.view = itemView;
        this.fireBaseManager = fireBaseManager;

        mDisplayNameTextView = (TextView) itemView.findViewById(R.id.tv_display_name);
        mDateTextView = (TextView) itemView.findViewById(R.id.tv_date);
        mMessageTextView = (TextView) itemView.findViewById(R.id.tv_message);
        mImageView = ((ImageView) itemView.findViewById(R.id.iv_profile));

        itemView.setOnClickListener(this);
    }

    public void bindConversation(final Conversation conversation) {

        final Long count = FireBaseManager.unreadMap.get(conversation.getRoomName()) == null ? 0L : FireBaseManager.unreadMap.get(conversation.getRoomName());
        mConversation = conversation;

        if (count > 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.md_grey_300));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        mDisplayNameTextView.setText(conversation.getFriendDisplayName(ref.getRef()));
        mDateTextView.setText(DATE_FORMAT.format(conversation.getLastMessageDate()));
        mMessageTextView.setText(conversation.getLastMessage());

        setImage(conversation, mImageView);
    }

    private void setImage(Conversation conversation, ImageView imageView) {

        String dirPath = imageView.getContext().getFilesDir().getAbsolutePath() + File.separator + "pic-profile";

        File imgFile = new File(Environment.getExternalStorageDirectory() + "/" + dirPath + "/" + conversation.extractFriendUid(ref.getRef()) + ".jpg");

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        } else {
            Bitmap icon = BitmapFactory.decodeResource(imageView.getResources(),
                    R.mipmap.ic_friends);
            imageView.setImageBitmap(icon);
        }
    }

    @Override
    public void onClick(View v) {
        fireBaseManager.openRoom(mConversation.getRoomName());
    }

}
