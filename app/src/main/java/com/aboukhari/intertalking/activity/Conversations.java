package com.aboukhari.intertalking.activity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.adapter.ConversationListAdapter;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Conversations extends Fragment {

    private Firebase ref;
    private ValueEventListener connectedListener;
    private ListView listView;
    private ConversationListAdapter conversationListAdapter;
    Map<String, ChildEventListener> messageListenerMap = new HashMap<>();
    private Date lastRead = new Date(0L);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_friends, container, false);
        listView = (ListView) v.findViewById(android.R.id.list);
        ref = new Firebase(getString(R.string.firebase_url));
        return v;
    }


    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        Firebase query = ref.child("room_names");
        conversationListAdapter = new ConversationListAdapter(query, R.layout.item_conversation_list, getActivity());
        listView.setAdapter(conversationListAdapter);
        conversationListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(conversationListAdapter.getCount() - 1);
            }
        });



    }

    @Override
    public void onStop() {
        super.onStop();
        // ref.getRoot().child(".info/connected").removeEventListener(connectedListener);
        conversationListAdapter.cleanup();
    }

}
