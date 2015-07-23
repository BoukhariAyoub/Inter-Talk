package com.aboukhari.intertalking.activity;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.adapter.FriendListAdapter;
import com.aboukhari.intertalking.model.Chat;
import com.aboukhari.intertalking.model.Conversation;
import com.aboukhari.intertalking.model.Friend;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Friends extends Fragment {

    private Firebase ref;
    private ValueEventListener connectedListener;
    private ListView listView;
    private FriendListAdapter friendListAdapter;
    Map<String, ChildEventListener> messageListenerMap = new HashMap<>();
    private Date lastRead = new Date(0L);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_friends, container, false);

        listView = (ListView) v.findViewById(android.R.id.list);
        ref = new Firebase(getString(R.string.firebase_url));
        //checkFriends(AccessToken.getCurrentAccessToken());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = (Friend) listView.getItemAtPosition(position);


                // Create a new, auto-generated child of that chat location, and save our chat data there
                //ref.child(input).setValue(room);
                String roomName = createRoom(friend);


                Intent intent = new Intent(getActivity(), ChatRoom.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
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

        addMessageListeners();


    }

    @Override
    public void onStop() {
        super.onStop();
        // ref.getRoot().child(".info/connected").removeEventListener(connectedListener);
        friendListAdapter.cleanup();
    }

    private void checkFriends(AccessToken token) {
        Log.d("natija", "hnaaa " + token.toString());
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                token, new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                        for (int i = 0, size = jsonArray.length(); i < size; i++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String fbid = object.get("id").toString();
                                String uid = "facebook:" + fbid;
                                String name = object.get("name").toString();
                                String imageUrl = ((JSONObject) ((JSONObject) object.get("picture")).get("data")).get("url").toString();

                                Friend friend = new Friend(uid, name, imageUrl);
                                downloadImage(uid, imageUrl);
                                ref.child("users").child(ref.getAuth().getUid()).child("friends").child("facebook:" + uid).setValue(friend);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void downloadImage(String uid, final String url) {
        String dirPath = getActivity().getFilesDir().getAbsolutePath() + File.separator + "pic-profile";

        File file = new File(Environment.getExternalStorageDirectory() + "/" + dirPath + "/" + uid + ".jpg");
        if (file.exists()) {
            file.delete();
        }

        DownloadManager dm = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(dirPath, uid + ".jpg");
        dm.enqueue(request);
    }

    private String setupRoomName(String myId, String friendId) {
        if (myId.compareTo(friendId) < 0) {
            return myId + "_" + friendId;
        }
        return friendId + "_" + myId;
    }

    private void addMessageListeners() {
        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {

                    if (messageListenerMap.containsKey(snap.getKey())) {

                    } else {

                        final String roomName = snap.getKey();
                        updateLastRead(roomName, snap);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    private void updateLastRead(final String roomName, final DataSnapshot snap) {

        Log.d("natija room", roomName);
        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Long d = (Long) dataSnapshot.getValue();
                    lastRead = new Date(d);
                    ChildEventListener messageListener = ref.getRoot().child("messages").child(snap.getKey()).orderByChild("date").startAt(d).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            final Chat chat = dataSnapshot.getValue(Chat.class);
                            ref.getRoot().child("users").child(chat.getAuthor()).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    showNotification(dataSnapshot.getValue().toString(), chat.getMessage());
                                    updateRoom(roomName);
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(500);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            Log.d("natija", "child changed = " + dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    messageListenerMap.put(snap.getKey(), messageListener);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void removeListeners(String roomName) {
        if (messageListenerMap.containsKey(roomName)) {
            ChildEventListener listener = messageListenerMap.get(roomName);
            ref.getRoot().child("messages").child(roomName).removeEventListener(listener);
            messageListenerMap.remove(roomName);
        }
    }

    public void showNotification(String title, String text) {
        //  PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Friends.class), 0);
        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(getActivity())
                .setTicker("Ticker")
                .setLargeIcon(BitmapFactory.decodeResource(r, R.mipmap.chat_logo))
                .setSmallIcon(R.mipmap.chat_logo)
                .setContentTitle(title)
                .setContentText(text)
                        //   .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private String createRoom(Friend friend) {
        Conversation conversation = new Conversation(ref, ref.getAuth().getUid(), friend.getuId());

        ref.child("room_names").child(conversation.getRoomName()).setValue(conversation);
        removeListeners(conversation.getRoomName());
        ref.child("users").child(conversation.getMyId()).child("rooms").child(conversation.getRoomName()).setValue(Calendar.getInstance().getTime());
        ref.child("users").child(friend.getuId()).child("rooms").child(conversation.getRoomName()).setValue(0);

        return conversation.getRoomName();
    }

    private void updateRoom(final String roomName) {

        ref.getRoot().child("messages").child(roomName).orderByChild("date").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Log.d("natija conv ", String.valueOf(dataSnapshot));
                    Map<String, Chat> value = (Map<String, Chat>) dataSnapshot.getValue();
                    Object key = value.keySet().toArray()[0];
                    Map<String, Object> chat = (Map<String, Object>) value.get(key);

                    Map<String, Object> map = new HashMap<>();
                    map.put("lastMessage", chat.get("message").toString());
                    map.put("lastMessageDate", chat.get("date"));

                    ref.getRoot().child("room_names").child(roomName).updateChildren(map);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }
}
