package com.aboukhari.intertalking.model;

import android.util.Log;

import com.aboukhari.intertalking.Utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aboukhari on 23/07/2015.
 */

@DatabaseTable(tableName = "conversation")

public class Conversation {

    @DatabaseField(id = true)
    String roomName;

    @DatabaseField
    String firstUid;

    @DatabaseField
    String secondUid;

    @DatabaseField
    String firstDisplayName;

    @DatabaseField
    String secondDisplayName;

    @DatabaseField
    Date dateCreated;

    @DatabaseField
    String lastMessage;

    @DatabaseField
    Date lastMessageDate;


    public Conversation() {
    }

    public Conversation(Firebase ref, String myId, String friendId, String firstDisplayName, String secondDisplayName) {
        firstUid = myId;
        secondUid = friendId;
        roomName = Utils.setupRoomName(myId, friendId);
        dateCreated = new Date();
        lastMessage = "";
        lastMessageDate = new Date(0);
        updateLastMessage(ref);
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getFirstUid() {
        return firstUid;
    }

    public void setFirstUid(String firstUid) {
        this.firstUid = firstUid;
    }

    public String getSecondUid() {
        return secondUid;
    }

    public void setSecondUid(String secondUid) {
        this.secondUid = secondUid;
    }

    public String getFirstDisplayName() {
        return firstDisplayName;
    }

    public void setFirstDisplayName(String firstDisplayName) {
        this.firstDisplayName = firstDisplayName;
    }

    public String getSecondDisplayName() {
        return secondDisplayName;
    }

    public void setSecondDisplayName(String secondDisplayName) {
        this.secondDisplayName = secondDisplayName;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "lastMessage='" + lastMessage + '\'' +
                ", lastMessageDate=" + lastMessageDate +
                ", roomName='" + roomName + '\'' +
                '}';
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

    private void updateLastMessage(final Firebase ref) {
        ref.getRoot().child("messages").child(roomName).orderByChild("date").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Log.d("natija conv ", String.valueOf(dataSnapshot));
                    Map<String, Message> value = (Map<String, Message>) dataSnapshot.getValue();
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

    private void extractFriendName(Firebase ref) {
        String friendId = extractFriendUid(ref);
        ref.child("users").child(friendId).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                secondDisplayName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public String extractFriendUid(Firebase ref) {
        String[] split = roomName.split("_");
        String string = ref.getAuth().getUid().equals(split[0]) ? split[1] : split[0];
        return string;
    }

    public String getFriendDisplayName(Firebase ref) {
        String displayName = ref.getAuth().getUid().equals(secondUid) ? firstDisplayName :  secondDisplayName;
        return displayName;
    }
}
