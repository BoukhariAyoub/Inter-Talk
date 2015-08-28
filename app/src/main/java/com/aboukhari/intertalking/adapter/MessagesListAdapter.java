package com.aboukhari.intertalking.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Utils;
import com.aboukhari.intertalking.model.Message;
import com.aboukhari.intertalking.model.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.github.siyamed.shapeimageview.RoundedImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class MessagesListAdapter extends BaseAdapter {

    private Query ref;
    public LayoutInflater inflater;
    public List<Message> models;
    private Map<String, Message> modelNames;
    private ChildEventListener listener;
    private String uid;
    private Activity activity;
    private static final DateFormat CHAT_MSG_DATE_FORMAT = new SimpleDateFormat("HH:mm");


    /**
     * @param ref        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                   combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     *                   instance of the corresponding view with the data from an instance of modelClass.
     * @param activity   The activity containing the ListView
     */
    public MessagesListAdapter(Query ref, Activity activity,String uid) {
        this.ref = ref;
        inflater = activity.getLayoutInflater();
        models = new ArrayList<Message>();
        modelNames = new HashMap<String, Message>();
        this.uid = uid;
        this.activity = activity;


        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
        listener = this.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                Message message = dataSnapshot.getValue(Message.class);
                modelNames.put(dataSnapshot.getKey(), message);

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    models.add(0, message);
                } else {
                    Message previousMessage = modelNames.get(previousChildName);
                    int previousIndex = models.indexOf(previousMessage);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == models.size()) {
                        models.add(message);
                    } else {
                        models.add(nextIndex, message);
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // One of the models changed. Replace it in our list and name mapping
                String modelName = dataSnapshot.getKey();
                Message oldMessage = modelNames.get(modelName);
                Message newMessage = dataSnapshot.getValue(Message.class);
                int index = models.indexOf(oldMessage);

                Log.d("natija message", "oldmodel = " + oldMessage);
                Log.d("natija message","newModel = " +newMessage);
                Log.d("natija message","index = " +index);

                models.set(index, newMessage);
                modelNames.put(modelName, newMessage);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                String modelName = dataSnapshot.getKey();
                Message oldMessage = modelNames.get(modelName);
                models.remove(oldMessage);
                modelNames.remove(modelName);
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                String modelName = dataSnapshot.getKey();
                Message oldMessage = modelNames.get(modelName);
                Message newMessage = dataSnapshot.getValue(Message.class);
                int index = models.indexOf(oldMessage);
                models.remove(index);
                if (previousChildName == null) {
                    models.add(0, newMessage);
                } else {
                    Message previousModel = modelNames.get(previousChildName);
                    int previousIndex = models.indexOf(previousModel);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == models.size()) {
                        models.add(newMessage);
                    } else {
                        models.add(nextIndex, newMessage);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur becaaause : " + firebaseError.getMessage());
            }
        });
    }

    public void cleanup() {
        // We're being destroyed, let go of our listener and forget about all of the models
        ref.removeEventListener(listener);
        models.clear();
        modelNames.clear();
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int i) {
        return models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = null;

        Message message = models.get(i);
        String author = message.getAuthor();


        if (view == null) {
            if (author.equals(uid)) {
                view = inflater.inflate(R.layout.item_chat_right, viewGroup, false);
            } else {
                view = inflater.inflate(R.layout.item_chat_left, viewGroup, false);
            }
        }

        String imageUrl;

        if (author.equals(uid)) {
            imageUrl = Utils.getUserFromPreferences(activity).getImageUrl();
        } else {
           User friend=  activity.getIntent().getParcelableExtra("friend");
            imageUrl =friend.getImageUrl() != null ?  friend.getImageUrl():"";
        }

        TextView messageText = (TextView) view.findViewById(R.id.message);
        TextView dateText = (TextView) view.findViewById(R.id.date);
        RoundedImageView imageView = (RoundedImageView) view.findViewById(R.id.iv_avatar);

        messageText.setText(message.getMessage());

        CHAT_MSG_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        dateText.setText(CHAT_MSG_DATE_FORMAT.format(message.getDate()));

        Utils.loadImage(activity, imageUrl, imageView);


        return view;
    }


}