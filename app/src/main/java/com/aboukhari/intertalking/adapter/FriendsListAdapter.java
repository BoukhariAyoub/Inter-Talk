package com.aboukhari.intertalking.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.Utils;
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
 * Created by aboukhari on 21/08/2015.
 */
public class FriendsListAdapter extends BaseAdapter {

    private Query ref;
    public int layout;
    public LayoutInflater inflater;
    public List<User> models;
    private Map<String, User> modelNames;
    private ChildEventListener listener;
    Activity activity;

    /**
     * @param ref      The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                 combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param activity The activity containing the ListView
     */
    public FriendsListAdapter(final Query ref, Activity activity) {
        this.ref = ref;
        this.activity = activity;
        inflater = activity.getLayoutInflater();
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

        if (view == null) {
            view = inflater.inflate(R.layout.item_friend_list, viewGroup, false);
        }

        User model = models.get(i);
        populateView(view, model);
        return view;
    }


    protected void populateView(final View view, final User friend) {

        ref.getRef().getRoot().child("users").child(friend.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("displayName").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    TextView displayNameTextView = (TextView) view.findViewById(R.id.tv_display_name);
                    displayNameTextView.setText(name);
                    ImageView imageView = ((ImageView) view.findViewById(R.id.iv_profile));
                    Utils.loadImage(imageUrl, imageView);
                } else {
                    ref.getRef().getRoot().child("users").child(ref.getRef().getAuth().getUid()).child("friends").child(friend.getUid()).removeValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("natija data", "cancel " + firebaseError);
            }
        });
    }
}
