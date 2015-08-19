package com.aboukhari.intertalking.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.CircularImageView;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.model.Message;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ChatListAdapter extends FirebaseListAdapter<Message> {

    private static final DateFormat CHAT_MSG_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    // The USERNAME for this client. We use this to indicate which messages originated from this user
    private String uid;
    private Firebase ref;
    private Activity activity;

    public ChatListAdapter(Query ref, Activity activity, int layout, String uid) {
        super(ref, Message.class, layout, activity);
        this.uid = uid;
        this.ref = ref.getRef().getRoot();
        this.activity = activity;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = null;

        Message message = models.get(i);
        String author = message.getAuthor();


        if (view == null) {
            if (author.equals(uid)) {
                view = inflater.inflate(layout, viewGroup, false);
            } else {
                view = inflater.inflate(R.layout.item_chat_left, viewGroup, false);
            }
        }

        String imageUrl;
        
        if (author.equals(uid)) {
            imageUrl = Utils.getUserFromPreferences(activity).getImageUrl();
        } else {
            imageUrl = activity.getIntent().getStringExtra("imageUrl") != null ?  activity.getIntent().getStringExtra("imageUrl"):"";
        }

        TextView messageText = (TextView) view.findViewById(R.id.message);
        TextView dateText = (TextView) view.findViewById(R.id.date);
        CircularImageView imageView = (CircularImageView) view.findViewById(R.id.iv_avatar);

        messageText.setText(message.getMessage());

        CHAT_MSG_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        dateText.setText(CHAT_MSG_DATE_FORMAT.format(message.getDate()));

        Utils.setImage(activity,imageUrl, imageView);


        return view;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view    A view instance corresponding to the layout we passed to the constructor.
     * @param message An instance representing the current state of a chat message
     */


    @Override
    protected void populateView(final View view, ViewGroup viewGroup, final Message message) {
    }
}