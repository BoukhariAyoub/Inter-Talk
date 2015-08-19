package com.aboukhari.intertalking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.holder.FindUserViewHolder;
import com.aboukhari.intertalking.model.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aboukhari on 17/08/2015.
 */
public class FindUsersRecyclerAdapter extends RecyclerView.Adapter<FindUserViewHolder> implements View.OnClickListener {


    private List<User> models;
    Query ref;
    private Map<String, User> modelNames;
    private ChildEventListener listener;
    private Context mContext;

    public FindUsersRecyclerAdapter(Context context,Query ref) {
        this.ref = ref;
        this.mContext = context;
        models = new ArrayList<>();
        modelNames = new HashMap<>();


        this.listener = this.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, final String previousChildName) {

                final String name = dataSnapshot.getKey();
                final User model = dataSnapshot.getValue(User.class);
                modelNames.put(name, model);
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
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // One of the models changed. Replace it in our list and name mapping
                final String modelName = dataSnapshot.getKey();
                final User oldModel = modelNames.get(modelName);
                final User newModel = dataSnapshot.getValue(User.class);


                int index = models.indexOf(oldModel);

                models.set(index, newModel);
                modelNames.put(modelName, newModel);

                notifyDataSetChanged();


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                final String modelName = dataSnapshot.getKey();
                final User oldModel = modelNames.get(modelName);


                modelNames.put(modelName, oldModel);
                models.remove(oldModel);
                modelNames.remove(modelName);


                notifyDataSetChanged();


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, final String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                final String modelName = dataSnapshot.getKey();
                final User oldModel = modelNames.get(modelName);
                final User newModel = dataSnapshot.getValue(User.class);


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

    }

    @Override
    public FindUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_search, parent, false);
        return new FindUserViewHolder(mContext,view);
    }

    @Override
    public void onBindViewHolder(FindUserViewHolder holder, int position) {
        Log.d("natija","bind position = " + position);
        final User user = models.get(position);
        holder.bindUser(user);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    @Override
    public long getItemId(int position) {
        return models.get(position).hashCode();
    }

    @Override
    public void onClick(View v) {

    }

    public void cleanup() {
        // We're being destroyed, let go of our listener and forget about all of the models
        ref.removeEventListener(listener);
        models.clear();
        modelNames.clear();
    }
}
