package com.aboukhari.intertalking.activity;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.Utils.FireBaseManager;
import com.aboukhari.intertalking.activity.main.Conversations;
import com.aboukhari.intertalking.adapter.ChatListAdapter;
import com.aboukhari.intertalking.database.DatabaseManager;
import com.aboukhari.intertalking.model.Message;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class ChatRoom extends Activity {

    private Firebase ref;
    private ValueEventListener connectedListener;
    private ChatListAdapter chatListAdapter;
    private ListView listView;
    private String roomName;
    private String mTitle;
    private DatabaseManager databaseManager;
    private FireBaseManager fireBaseManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        databaseManager = DatabaseManager.getInstance(this);

        fireBaseManager = new FireBaseManager(this);
        roomName = getIntent().getStringExtra("roomName");
        mTitle = getIntent().getStringExtra("title");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(mTitle);
        }

        listView = (ListView) findViewById(android.R.id.list);


        FireBaseManager.currentRoom = roomName;

        // Setup our Firebase ref
        ref = new Firebase(getString(R.string.firebase_url));

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        fireBaseManager.updateLastRead(roomName);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        // Tell our list adapter that we only want 50 messages at a time
        chatListAdapter = new ChatListAdapter(ref.child("messages").child(roomName).orderByChild("date"), this, R.layout.item_chat_list, ref.getAuth().getUid());
        listView.setAdapter(chatListAdapter);
        chatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                // Log.d("natija", "gotMessage");
                listView.setSelection(chatListAdapter.getCount() - 1);
            }
        });


        // Finally, a little indication of connection status
        connectedListener = ref.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(ChatRoom.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatRoom.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        FireBaseManager.currentRoom = roomName;
        FireBaseManager.currentRoom = "hoho";//TODO delete this
    }

    @Override
    public void onStop() {
        super.onStop();
        ref.getRoot().child(".info/connected").removeEventListener(connectedListener);
        chatListAdapter.cleanup();
        fireBaseManager.updateLastRead(roomName);
        fireBaseManager.checkUnread(roomName);
    Conversations.conversationsRecyclerAdapter.notifyDataSetChanged();
        FireBaseManager.currentRoom = null;
    }


    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            Message message = new Message(input, ref.getAuth().getUid());
            ref.child("messages").child(roomName).push().setValue(message);
            inputText.setText("");
        }
    }


}
