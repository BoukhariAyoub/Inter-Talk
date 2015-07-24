package com.aboukhari.intertalking.model;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aboukhari on 23/07/2015.
 */
public class Conversation {

    String roomName;
    String myId;
    String friendId;
    Date dateCreated;
    String lastMessage;
    Date lastMessageDate;


    public Conversation() {
    }

    public Conversation(Firebase ref, String myId, String friendId) {
        this.myId = myId;
        this.friendId = friendId;
        this.roomName = setupRoomName(myId, friendId);
        this.dateCreated = Calendar.getInstance().getTime();
        this.lastMessage = "";
        this.lastMessageDate = new Date(0);

        updateLastMessage(ref);
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    private String setupRoomName(String myId, String friendId) {
        if (myId.compareTo(friendId) < 0) {
            return myId + "_" + friendId;
        }
        return friendId + "_" + myId;
    }

    private void updateLastMessage(final Firebase ref) {
        ref.getRoot().child("messages").child(roomName).orderByChild("date").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Log.d("natija conv ", String.valueOf(dataSnapshot));
                    Map<String, Chat> value = (Map<String, Chat>) dataSnapshot.getValue();
                    Object key = value.keySet().toArray()[0];
                    Map<String, Object> chat = (Map<String, Object>) value.get(key);

                    lastMessage = (String) chat.get("message");
                    lastMessageDate = new Date((Long) chat.get("date"));


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

    public String extractFriendUid(Firebase ref) {
        return myId.equals(ref.getAuth().getUid()) ? friendId:myId;
    }
}
