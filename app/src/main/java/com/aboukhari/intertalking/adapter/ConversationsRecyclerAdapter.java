package com.aboukhari.intertalking.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.DateComparator;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.model.Conversation;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aboukhari on 31/07/2015.
 */
public class ConversationsRecyclerAdapter extends RecyclerView.Adapter<ConversationsHolder> {

    Query ref;
    private List<Conversation> models;
    private Map<String, Conversation> modelNames;
    private ChildEventListener listener;
    private FireBaseManager fireBaseManager;


    public ConversationsRecyclerAdapter(Query ref, FireBaseManager fireBaseManager) {
        this.ref = ref;
        this.fireBaseManager = fireBaseManager;

        models = new ArrayList<>();
        modelNames = new HashMap<>();
        final String uid = ConversationsRecyclerAdapter.this.ref.getRef().getAuth().getUid();
        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
        listener = this.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, final String previousChildName) {

                final String roomName = dataSnapshot.getKey();
                final Conversation model = dataSnapshot.getValue(Conversation.class);
                Log.d("natija", dataSnapshot.toString());
                ConversationsRecyclerAdapter.this.ref.getRef().getRoot().child("users").child(uid).child("rooms").child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.exists()) {
                            modelNames.put(roomName, model);
                            // Insert into the correct location, based on previousChildName
                            if (previousChildName == null) {
                                models.add(0, model);
                            } else {
                                Conversation previousModel = modelNames.get(previousChildName);
                                int previousIndex = models.indexOf(previousModel);
                                int nextIndex = previousIndex + 1;
                                if (nextIndex == models.size()) {
                                    models.add(model);
                                } else {
                                    models.add(nextIndex, model);
                                }
                            }

                            Collections.sort(models, new DateComparator());

                            notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // One of the models changed. Replace it in our list and name mapping
                final String modelName = dataSnapshot.getKey();
                final Conversation oldModel = modelNames.get(modelName);
                final Conversation newModel = dataSnapshot.getValue(Conversation.class);

                ConversationsRecyclerAdapter.this.ref.getRef().getRoot().child("users").child(uid).child("rooms").child(modelName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int index = models.indexOf(oldModel);

                        models.set(index, newModel);
                        modelNames.put(modelName, newModel);

                        Collections.sort(models, new DateComparator());
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                final String modelName = dataSnapshot.getKey();
                final Conversation oldModel = modelNames.get(modelName);

                ConversationsRecyclerAdapter.this.ref.getRef().getRoot().child("users").child(uid).child("rooms").child(modelName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            modelNames.put(modelName, oldModel);
                            models.remove(oldModel);
                            modelNames.remove(modelName);

                            Collections.sort(models, new DateComparator());

                            notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, final String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                final String modelName = dataSnapshot.getKey();
                final Conversation oldModel = modelNames.get(modelName);
                final Conversation newModel = dataSnapshot.getValue(Conversation.class);

                ConversationsRecyclerAdapter.this.ref.getRef().getRoot().child("users").child(uid).child("rooms").child(modelName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            int index = models.indexOf(oldModel);
                            models.remove(index);
                            if (previousChildName == null) {
                                models.add(0, newModel);
                            } else {
                                Conversation previousModel = modelNames.get(previousChildName);
                                int previousIndex = models.indexOf(previousModel);
                                int nextIndex = previousIndex + 1;
                                if (nextIndex == models.size()) {
                                    models.add(newModel);
                                } else {
                                    models.add(nextIndex, newModel);
                                }
                            }
                            Collections.sort(models, new DateComparator());

                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
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
    public ConversationsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation_list, parent, false);
        return new ConversationsHolder(view, ref, fireBaseManager);
    }


    @Override
    public void onBindViewHolder(ConversationsHolder holder, int position) {
        Conversation conversation = models.get(position);
        holder.bindConversation(conversation);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public long getItemId(int position) {
        return models.get(position).hashCode();
    }
}
