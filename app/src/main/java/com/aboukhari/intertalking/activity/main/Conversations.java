package com.aboukhari.intertalking.activity.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.adapter.ConversationsRecyclerAdapter;
import com.aboukhari.intertalking.adapter.MyGridLayoutManager;
import com.aboukhari.intertalking.adapter.MyLayoutManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;


public class Conversations extends Fragment {

    private Firebase ref;
    private ValueEventListener connectedListener;
    private ListView listView;
    private RecyclerView recyclerView;
    // public static ConversationsListAdapter conversationListAdapter;
    public static ConversationsRecyclerAdapter conversationsRecyclerAdapter;

    Map<String, ChildEventListener> messageListenerMap = new HashMap<>();
    private FireBaseManager fireBaseManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_conversations, container, false);
        listView = (ListView) v.findViewById(android.R.id.list);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        Firebase.setAndroidContext(getActivity());
        ref = new Firebase(getString(R.string.firebase_url));


        MainActivity activity = (MainActivity) getActivity();
        fireBaseManager = activity.getFireBaseManager();
        fireBaseManager.theRealDeal();


     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conversation conversation = (Conversation) listView.getItemAtPosition(position);
                fireBaseManager.openRoom(conversation.getRoomName());
            }

        });*/

        return v;
    }


    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        Query query = ref.child("room_names");//.orderByChild("myId").equalTo(ref.getAuth().getUid());
     /*   conversationListAdapter = new ConversationsListAdapter(query, R.layout.item_conversation_list, getActivity());
        listView.setAdapter(conversationListAdapter);
        conversationListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(conversationListAdapter.getCount() - 1);
            }
        });*/
        conversationsRecyclerAdapter = new ConversationsRecyclerAdapter(getActivity(),query, fireBaseManager);
        conversationsRecyclerAdapter.setHasStableIds(true);
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(),1);
        MyLayoutManager myLayoutManager = new MyLayoutManager(getActivity());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(conversationsRecyclerAdapter);
        recyclerView.setItemAnimator(new FadeInAnimator());



        // listView.setAdapter(conversationListAdapter);
    /*    conversationListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(conversationListAdapter.getCount() - 1);
            }
        });*/


    }

    @Override
    public void onStop() {
        super.onStop();
        conversationsRecyclerAdapter.cleanup();
        //  conversationListAdapter.cleanup();
    }


}
