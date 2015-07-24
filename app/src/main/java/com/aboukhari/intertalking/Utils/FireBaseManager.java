package com.aboukhari.intertalking.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.activity.ChatRoom;
import com.aboukhari.intertalking.model.Chat;
import com.aboukhari.intertalking.model.Conversation;
import com.aboukhari.intertalking.model.Friend;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aboukhari on 24/07/2015.
 */
public class FireBaseManager {

    Firebase ref;
    Map<String, ChildEventListener> messageListenerMap = new HashMap<>();
    public static Map<String, Long> unreadMap = new HashMap();
    Context context;
    public static String currentRoom = null;


    public FireBaseManager(Context context) {
        Firebase.setAndroidContext(context);
        this.ref = new Firebase(context.getResources().getString(R.string.firebase_url));
        this.context = context;
    }


    /**
     * Create Room if not exists
     *
     * @param friend
     * @return
     */
    public String createRoom(final Friend friend) {
        final Conversation conversation = new Conversation(ref, ref.getAuth().getUid(), friend.getuId());

        ref.child("room_names").child(conversation.getRoomName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {

                    //Add Room to room_names
                    ref.child("room_names").child(conversation.getRoomName()).setValue(conversation);

                    //Add Room to current user Rooms
                    ref.child("users").child(conversation.getMyId()).child("rooms").child(conversation.getRoomName()).setValue(Calendar.getInstance().getTime());

                    //Add Room to friend's Rooms
                    ref.child("users").child(friend.getuId()).child("rooms").child(conversation.getRoomName()).setValue(0);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return conversation.getRoomName();
    }


    /**
     * Update last message and last message date of conversation
     *
     * @param roomName
     */

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


    public void openRoom(String roomName) {

        Intent intent = new Intent(context, ChatRoom.class);
        intent.putExtra("roomName", roomName);
        context.startActivity(intent);
    }

    public void addMessageListeners() {
        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (final DataSnapshot snap : snapshot.getChildren()) {

                    if (!messageListenerMap.containsKey(snap.getKey())) {

                        final String roomName = snap.getKey();
                        //Add listener to Read


                        //   final Long d =  addReadListener(roomName);

                        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").child(roomName).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    //Last Read by user
                                    Long read = dataSnapshot.getValue(Long.class);
                                    ChildEventListener messageListener = attachMessageListener(roomName, snap, read);
                                    checkLastMessages(roomName, read);

                                    messageListenerMap.put(roomName, messageListener);
                                    //Get All the messages since last read and attach a listener

                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }


    /**
     * Attach Listener To unread Messages
     *
     * @param roomName
     * @param snap
     * @param lastRead
     * @return
     */

    private ChildEventListener attachMessageListener(final String roomName, final DataSnapshot snap, final Long lastRead) {

        return ref.getRoot().child("messages").child(snap.getKey()).orderByChild("date").startAt(lastRead).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Chat chat = dataSnapshot.getValue(Chat.class);
                //  Log.d("natija unread messages", chat.getMessage());
                //Get Sender display Name
                ref.getRoot().child("users").child(chat.getAuthor()).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Show Notification and update Room
                        if (!roomName.equals(currentRoom)) {

                            showNotification(dataSnapshot.getValue().toString(), chat.getMessage());
                            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(500);
                           checkLastMessages(roomName, lastRead);

                        }
                        updateRoom(roomName);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


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
    }


    public void showNotification(String title, String text) {
        //  PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Friends.class), 0);
        Resources r = context.getResources();
        Notification notification = new NotificationCompat.Builder(context)
                .setTicker("Ticker")
                .setLargeIcon(BitmapFactory.decodeResource(r, R.mipmap.chat_logo))
                .setSmallIcon(R.mipmap.chat_logo)
                .setContentTitle(title)
                .setContentText(text)
                        //   .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }


    /**
     * Remove New Message Listener
     *
     * @param roomName
     */
    private void removeListeners(String roomName) {
        if (messageListenerMap.containsKey(roomName)) {
            ChildEventListener listener = messageListenerMap.get(roomName);
            ref.getRoot().child("messages").child(roomName).removeEventListener(listener);
            messageListenerMap.remove(roomName);
        }
    }

    private void checkLastMessages(final String roomName, final Long lastRead) {
        final Date d = new Date(lastRead);

        ref.getRoot().child("messages").child(roomName).orderByChild("date").startAt(lastRead).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                Log.d("natija unread messages", "last read date " + roomName +" - " + lastRead);
                Log.d("natija unread messages", "last read long "+ roomName +" - " + d.toString());
                Log.d("natija unread messages", "last read count "+ roomName +" - "  + count);
                unreadMap.put(roomName, count);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public Long addReadListener(final String roomName) {

        final Long[] t = new Long[1];
        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").child(roomName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long read = dataSnapshot.getValue(Long.class);
                checkLastMessages(roomName, read);
                t[0] = read;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return t[0];
    }
}
