package com.aboukhari.intertalking.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Constants;
import com.aboukhari.intertalking.Utils.TranslationManager;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.activity.ChatRoom;
import com.aboukhari.intertalking.model.Message;
import com.aboukhari.intertalking.model.User;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.TimeZone;

/**
 * Created by aboukhari on 02/09/2015.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    Message message;
    TextView messageTextView;
    TextView dateTextView;
    CircularImageView circularImageView;
    ChatRoom activity;
    String uid;

    public MessageViewHolder(View view, ChatRoom activity, String uid) {
        super(view);
        this.uid = uid;
        this.activity = activity;
        messageTextView = (TextView) view.findViewById(R.id.message);
        dateTextView = (TextView) view.findViewById(R.id.date);
        circularImageView = (CircularImageView) view.findViewById(R.id.iv_avatar);
    }

    public MessageViewHolder bind(Message message, String messageId) {
        this.message = message;

        String author = message.getAuthor();


        String imageUrl;

        String text = message.getMessage();

        if (author.equals(uid)) {
            imageUrl = Utils.getUserFromPreferences(activity).getImageUrl();
            messageTextView.setText(text);

        } else {
            User friend = activity.getIntent().getParcelableExtra("friend");
            imageUrl = friend.getImageUrl() != null ? friend.getImageUrl() : "";

            if( message.getDate().after(activity.getEnabledDate())){
                TranslationManager.translateMessage(activity, "en-fr", messageId, text, messageTextView);
            }
            else{
                messageTextView.setText(text);
            }
        }






        Constants.MESSAGE_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        dateTextView.setText(Constants.MESSAGE_DATE_FORMAT.format(message.getDate()));

        Utils.loadImage(activity, imageUrl, circularImageView);
        return this;

    }

    @Override
    public void onClick(View v) {

    }
}