package com.aboukhari.intertalking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.holder.FriendsViewHolder;
import com.aboukhari.intertalking.model.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ayoub.boukhari on 03/12/2015.
 */
public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsViewHolder> {


    Query ref;
    private List<User> models;
    private Map<String, User> modelNames;
    private ChildEventListener listener;
    private FireBaseManager fireBaseManager;
    private Context context;

    public FriendsRecyclerAdapter(Context context, final Query ref, final FireBaseManager fireBaseManager) {
        this.ref = ref;
        this.context = context;
        this.fireBaseManager = fireBaseManager;

        models = new ArrayList<>();
        modelNames = new HashMap<>();
        this.ref = ref;
        models = new ArrayList<>();
        modelNames = new HashMap<>();
        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
        listener = this.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, final String previousChildName) {

                String friendUid = dataSnapshot.getKey();
                Boolean isAccepted = dataSnapshot.getValue(Boolean.class);
                if (isAccepted) {
                    ref.getRef().getRoot().child("users").child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User model = dataSnapshot.getValue(User.class);

                            modelNames.put(dataSnapshot.getKey(), model);

                            // Insert into the correct location, based on previousChildName
                            if (previousChildName == null) {
                                models.add(0, model);
                            } else {
                                User previousModel = modelNames.get(previousChildName);
                                int previousIndex = models.indexOf(previousModel);
                                int nextIndex = previousIndex + 1;
                                if (nextIndex == models.size()) {
                                    models.add(model);
                                } else {
                                    models.add(nextIndex, model);
                                }
                            }

                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                } //END if Accepted
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String friendUid = dataSnapshot.getKey();
                Boolean isAccepted = dataSnapshot.getValue(Boolean.class);
                final String modelName = dataSnapshot.getKey();
                final User oldModel = modelNames.get(modelName);

                if (isAccepted) {
                    ref.getRef().getRoot().child("users").child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // One of the models changed. Replace it in our list and name mapping
                            User newModel = dataSnapshot.getValue(User.class);
                            int index = models.indexOf(oldModel);

                            models.set(index, newModel);
                            modelNames.put(modelName, newModel);

                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                } //END if Accepted
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                String modelName = dataSnapshot.getKey();
                User oldModel = modelNames.get(modelName);
                models.remove(oldModel);
                modelNames.remove(modelName);
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, final String previousChildName) {

                String friendUid = dataSnapshot.getKey();
                Boolean isAccepted = dataSnapshot.getValue(Boolean.class);
                final String modelName = dataSnapshot.getKey();
                final User oldModel = modelNames.get(modelName);

                if (isAccepted) {
                    ref.getRef().getRoot().child("users").child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // One of the models changed. Replace it in our list and name mapping
                            User newModel = dataSnapshot.getValue(User.class);
                            int index = models.indexOf(oldModel);
                            models.remove(index);
                            if (previousChildName == null) {
                                models.add(0, newModel);
                            } else {
                                User previousModel = modelNames.get(previousChildName);
                                int previousIndex = models.indexOf(previousModel);
                                int nextIndex = previousIndex + 1;
                                if (nextIndex == models.size()) {
                                    models.add(newModel);
                                } else {
                                    models.add(nextIndex, newModel);
                                }
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                } //END if Accepted
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
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_list, parent, false);
        return new FriendsViewHolder(context, view, fireBaseManager);
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {
        holder.bindConversation(models.get(position));
    }


    @Override
    public int getItemCount() {
        return models.size();
    }




}
