package com.aboukhari.intertalking.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.adapter.FriendListAdapter;
import com.aboukhari.intertalking.model.Friend;
import com.aboukhari.intertalking.model.Room;
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


public class Friends extends Activity {

    private Firebase ref;
    private ValueEventListener connectedListener;
    private ListView listView;
    private FriendListAdapter friendListAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        listView = (ListView) findViewById(android.R.id.list);
        ref = new Firebase(getString(R.string.firebase_url));
        //checkFriends(AccessToken.getCurrentAccessToken());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = (Friend) listView.getItemAtPosition(position);


                // Create a new, auto-generated child of that chat location, and save our chat data there
                //ref.child(input).setValue(room);


                String roomName = setupRoomName(ref.getAuth().getUid(), friend.getuId());
                Room room = new Room(roomName);
                ref.child("room_names").child(roomName).setValue(room);
                ref.child("users").child(ref.getAuth().getUid()).child("rooms").child(roomName).setValue(room);
                ref.child("users").child(friend.getuId()).child("rooms").child(roomName).setValue(room);
                Intent intent = new Intent(Friends.this,ChatRoom.class);
                intent.putExtra("roomName",roomName);
                startActivity(intent);
            }
        });


    }

    public void onStart () {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        Firebase query = ref.child("users").child(ref.getAuth().getUid()).child("friends");
        friendListAdapter = new FriendListAdapter(query, R.layout.item_friend_list, this);
        listView.setAdapter(friendListAdapter);
        friendListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(friendListAdapter.getCount() - 1);
            }
        });

        ref.getRoot().child("room_names").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("natija", "key = " + dataSnapshot.getKey());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
                                downloadImage(uid,imageUrl);
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
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + "pic-profile";

        File file = new File(Environment.getExternalStorageDirectory() + "/" + dirPath + "/" + uid + ".jpg");
        if(file.exists()){
            file.delete();
        }

        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
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
}
