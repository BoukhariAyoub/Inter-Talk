package com.aboukhari.intertalking.activity.main;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.adapter.FriendListAdapter;
import com.aboukhari.intertalking.model.Friend;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;


public class Friends extends Fragment {

    private Firebase ref;
    private ValueEventListener connectedListener;
    private ListView listView;
    private FriendListAdapter friendListAdapter;
    private FireBaseManager fireBaseManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_friends, container, false);


        listView = (ListView) v.findViewById(android.R.id.list);
        Firebase.setAndroidContext(getActivity());
        ref = new Firebase(getString(R.string.firebase_url));

        MainActivity activity = (MainActivity) getActivity();
        fireBaseManager = activity.getFireBaseManager();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = (Friend) listView.getItemAtPosition(position);
                fireBaseManager.createRoom(friend);
            }

        });
        return v;
    }


    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        Firebase query = ref.child("users").child(ref.getAuth().getUid()).child("friends");
        friendListAdapter = new FriendListAdapter(query, R.layout.item_friend_list, getActivity());
        listView.setAdapter(friendListAdapter);
        friendListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(friendListAdapter.getCount() - 1);
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        // ref.getRoot().child(".info/connected").removeEventListener(connectedListener);
        friendListAdapter.cleanup();
    }


}
