package com.aboukhari.intertalking.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.adapter.FriendsRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;


public class Friends extends Fragment {

    private Firebase ref;
    private ValueEventListener connectedListener;
    private FriendsRecyclerAdapter mFriendsRecyclerAdapter;
    private FireBaseManager fireBaseManager;
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_friends, container, false);


        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        Firebase.setAndroidContext(getActivity());
        ref = new Firebase(getString(R.string.firebase_url));

        Main3Activity activity = (Main3Activity) getActivity();
        fireBaseManager = activity.getFireBaseManager();


        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) listView.getItemAtPosition(position);
                fireBaseManager.createRoom(user);
            }

        });*/
        return v;
    }


    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        Firebase query = ref.child("users").child(ref.getAuth().getUid()).child("friends");

        mFriendsRecyclerAdapter = new FriendsRecyclerAdapter(getActivity(),query, fireBaseManager);
        mFriendsRecyclerAdapter.setHasStableIds(true);
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(),1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mFriendsRecyclerAdapter);
        mRecyclerView.setItemAnimator(new FadeInAnimator());




    }

    @Override
    public void onStop() {
        super.onStop();
        mFriendsRecyclerAdapter.cleanup();
    }


}
