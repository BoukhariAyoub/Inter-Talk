package com.aboukhari.intertalking.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.activity.ChatRoom;
import com.aboukhari.intertalking.holder.MessageViewHolder;
import com.aboukhari.intertalking.model.Message;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aboukhari on 02/09/2015.
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    Query ref;
    private List<Message> models;
    private Map<String, Message> modelNames;
    private List<String> messageIds;
    private ChildEventListener listener;
    private FireBaseManager fireBaseManager;
    ChatRoom activity;


    public MessageRecyclerAdapter(Query ref, FireBaseManager fireBaseManager, ChatRoom activity) {
        this.ref = ref;
        models = new ArrayList<>();
        messageIds = new ArrayList<>();
        modelNames = new HashMap<>();
        this.fireBaseManager = fireBaseManager;
        this.activity = activity;


        listener = this.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                Message message = dataSnapshot.getValue(Message.class);

                modelNames.put(dataSnapshot.getKey(), message);

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    models.add(0, message);
                    messageIds.add(0, dataSnapshot.getKey());
                    notifyItemInserted(0);
                } else {
                    Message previousMessage = modelNames.get(previousChildName);
                    int previousIndex = models.indexOf(previousMessage);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == models.size()) {
                        models.add(message);
                        messageIds.add(dataSnapshot.getKey());
                        notifyItemInserted(models.size() - 1);
                    } else {
                        models.add(nextIndex, message);
                        messageIds.add(nextIndex, dataSnapshot.getKey());
                        notifyItemInserted(nextIndex);

                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur because : " + firebaseError.getMessage());
            }
        });
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);

        return new MessageViewHolder(view, activity, ref.getRef().getAuth().getUid());
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = models.get(position);
        String messageId = messageIds.get(position);
        holder.bind(message, messageId);
    }

    public void cleanup() {
        // We're being destroyed, let go of our listener and forget about all of the models
        ref.removeEventListener(listener);
        models.clear();
        modelNames.clear();
        messageIds.clear();
    }


/*
    public void addItem(Message message,int position) {
        models.add(position, message);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, models.size());
    }*/



    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = models.get(position);
        boolean isMe = message.getAuthor().equalsIgnoreCase(ref.getRef().getAuth().getUid());
        int type = isMe ? R.layout.item_chat_right : R.layout.item_chat_left;
        return type;
    }
}
