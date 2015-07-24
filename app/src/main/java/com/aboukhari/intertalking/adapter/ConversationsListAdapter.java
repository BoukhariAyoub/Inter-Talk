package com.aboukhari.intertalking.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.DateComparator;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.model.Conversation;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a generic way of backing an Android ListView with a Firebase location.
 * It handles all of the child events at the given Firebase location. It marshals received data into the given
 * class type. Extend this class and provide an implementation of <code>populateView</code>, which will be given an
 * instance of your list item layout and an instance your class that holds your data. Simply populate the view however
 * you like and this class will handle updating the list as the data changes.
 */
public class ConversationsListAdapter extends BaseAdapter {

    private Query ref;
    private Class<Conversation> modelClass;
    private int layout;
    private LayoutInflater inflater;
    private List<Conversation> models;
    private Map<String, Conversation> modelNames;
    private ChildEventListener listener;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");


    /**
     * @param ref      The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                 combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param layout   This is the layout used to represent a single list item. You will be responsible for populating an
     *                 instance of the corresponding view with the data from an instance of modelClass.
     * @param activity The activity containing the ListView
     */
    public ConversationsListAdapter(Query ref, int layout, Activity activity) {


        this.ref = ref;
        this.modelClass = Conversation.class;
        this.layout = layout;
        inflater = activity.getLayoutInflater();
        models = new ArrayList<>();
        modelNames = new HashMap<>();
        final String uid = ConversationsListAdapter.this.ref.getRef().getAuth().getUid();
        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
        listener = this.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, final String previousChildName) {

                final String roomName = dataSnapshot.getKey();
                final Conversation model = dataSnapshot.getValue(ConversationsListAdapter.this.modelClass);
                Log.d("natija", dataSnapshot.toString());
                ConversationsListAdapter.this.ref.getRef().getRoot().child("users").child(uid).child("rooms").child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
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
                final Conversation newModel = dataSnapshot.getValue(ConversationsListAdapter.this.modelClass);

                ConversationsListAdapter.this.ref.getRef().getRoot().child("users").child(uid).child("rooms").child(modelName).addListenerForSingleValueEvent(new ValueEventListener() {
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

                ConversationsListAdapter.this.ref.getRef().getRoot().child("users").child(uid).child("rooms").child(modelName).addListenerForSingleValueEvent(new ValueEventListener() {
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
                final Conversation newModel = dataSnapshot.getValue(ConversationsListAdapter.this.modelClass);

                ConversationsListAdapter.this.ref.getRef().getRoot().child("users").child(uid).child("rooms").child(modelName).addListenerForSingleValueEvent(new ValueEventListener() {
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

            view = inflater.inflate(layout, viewGroup, false);
            Conversation model = models.get(i);
            // Call out to subclass to marshall this model into the provided view
            populateView(view, model);
        }


        return view;
    }


    protected void populateView(final View view, final Conversation conversation) {

        Log.d("natija", "populateView " + conversation);
        final Long count = FireBaseManager.unreadMap.get(conversation.getRoomName()) == null ? 0L : FireBaseManager.unreadMap.get(conversation.getRoomName());

        if (count > 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.md_grey_300));
        }
        ImageView imageView = ((ImageView) view.findViewById(R.id.iv_profile));
        setImage(conversation, imageView);
        ref.getRef().getRoot().child("users").child(conversation.extractFriendUid(ref.getRef())).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView) view.findViewById(R.id.tv_display_name)).setText(dataSnapshot.getValue().toString() + " - " + count);
                ((TextView) view.findViewById(R.id.tv_date)).setText(DATE_FORMAT.format(conversation.getLastMessageDate()));
                ((TextView) view.findViewById(R.id.tv_message)).setText(conversation.getLastMessage());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setImage(Conversation conversation, ImageView imageView) {

        String dirPath = imageView.getContext().getFilesDir().getAbsolutePath() + File.separator + "pic-profile";

        File imgFile = new File(Environment.getExternalStorageDirectory() + "/" + dirPath + "/" + conversation.extractFriendUid(ref.getRef()) + ".jpg");

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }
}