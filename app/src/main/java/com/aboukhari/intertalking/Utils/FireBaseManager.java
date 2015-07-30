package com.aboukhari.intertalking.Utils;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.activity.ChatRoom;
import com.aboukhari.intertalking.activity.Friends;
import com.aboukhari.intertalking.database.DatabaseManager;
import com.aboukhari.intertalking.model.Conversation;
import com.aboukhari.intertalking.model.Friend;
import com.aboukhari.intertalking.model.Message;
import com.aboukhari.intertalking.model.User;
import com.aboukhari.intertalking.model.UserRoom;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by aboukhari on 24/07/2015.
 */
public class FireBaseManager {

    Context context;
    private DatabaseManager databaseManager;
    Firebase ref;

    Map<String, ChildEventListener> messagesListenerMap = new HashMap<>();
    Map<String, ValueEventListener> singleMessageLisntenerMap = new HashMap<>();
    public static Map<String, Long> unreadMap = new HashMap();
    public static String currentRoom = null;
    Map<String, ChildEventListener> roomMessagesListenerMap = new HashMap<>();


    public FireBaseManager(Context context) {
        Firebase.setAndroidContext(context);
        this.ref = new Firebase(context.getResources().getString(R.string.firebase_url));
        this.context = context;
        databaseManager = DatabaseManager.getInstance(context);

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
                    databaseManager.addConversation(conversation);

                    //Add Room to current user Rooms
                    HashMap<String, Object> userRoomMap = new HashMap<>();
                    //userRoomMap.put("")
                    Date lastSeen = Calendar.getInstance().getTime();
                    ref.child("users").child(conversation.getMyId()).child("rooms").child(conversation.getRoomName()).setValue(lastSeen);
                    UserRoom userRoom = new UserRoom();
                    userRoom.setLastSeen(lastSeen);
                    userRoom.setFriend(friend);
                    userRoom.setRoomName(conversation.getRoomName());

                    //Add current room to database
                    databaseManager.addUserRoom(userRoom);

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

                if (dataSnapshot.exists()) {
                    Log.d("natija conv ", String.valueOf(dataSnapshot));
                    Map<String, Message> value = (Map<String, Message>) dataSnapshot.getValue();
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
        /* Check user's Rooms */
        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("natija listener", " ref.getRoot().child(\"users\").child(ref.getAuth().getUid()).child(\"rooms\")");

               /* Iterate User Rooms */
                for (final DataSnapshot snap : snapshot.getChildren()) {

                    Log.d("natija get ref", snap.getRef().getKey());
                    /* If listener isn't already added */
                    if (!messagesListenerMap.containsKey(snap.getKey())) {

                        final String roomName = snap.getKey();

                        /* Check last Read Message */
                        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long read = dataSnapshot.getValue(Long.class);
                                Log.d("natija listener", " ref.getRoot().child(\"users\").child(ref.getAuth().getUid()).child(\"rooms\").child(roomName) ");
                                databaseManager.updateLastSeen(roomName, read);
                                //   checkLastMessages(roomName, read);
                                //checkLastMessages(roomName, read);

                                if (messagesListenerMap.containsKey(roomName)) {
                                    ChildEventListener messageListener = attachMessageListener(roomName, snap, read);
                                    messagesListenerMap.put(roomName, messageListener);


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
        Log.d("natija unread date", "before key " + snap.getKey());

        return ref.getRoot().child("messages").child(snap.getKey()).orderByChild("date").startAt(lastRead).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Message message = dataSnapshot.getValue(Message.class);
                //  Log.d("natija unread messages", chat.getMessage());
                //Get Sender display Name
                //       ref.getRoot().child("users").child(message.getAuthor()).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                Log.d("natija unread date", "key " + dataSnapshot.getKey());
                //Show Notification and update Room
                if (!roomName.equals(currentRoom)) {
                    //   showNotification(dataSnapshot.getValue().toString(), message.getMessage());
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
                String messageId = dataSnapshot.getKey();

                if (messagesListenerMap.containsKey(messageId)) {
                    ChildEventListener listener = messagesListenerMap.get(messageId);
                    ref.getRoot().child("messages").child(roomName).child(messageId).removeEventListener(listener);
                    messagesListenerMap.remove(messageId);
                }
                messagesListenerMap.put(messageId, this);


                checkLastMessages(roomName, lastRead);


                updateRoom(roomName);

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
        if (messagesListenerMap.containsKey(roomName)) {
            ChildEventListener listener = messagesListenerMap.get(roomName);
            ref.getRoot().child("messages").child(roomName).removeEventListener(listener);
            messagesListenerMap.remove(roomName);
        }
    }

    private void checkLastMessages(final String roomName, final Long lastRead) {
        ref.getRoot().child("messages").child(roomName).orderByChild("date").startAt(lastRead).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                // Log.d("natija unread count", "last read count " + roomName + " - " + count);
                unreadMap.put(roomName, count);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void onFacebookAccessTokenChange(final AccessToken token, final String pictureUrl) {
        if (token != null) {

            ref.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(final AuthData authData) {

                    String email = authData.getProviderData().get("email").toString();

                    ref.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String uid = authData.getUid();
                            String email = "";
                            Date birthday = new Date(0L);
                            String displayName = "";
                            String gender = "";

                            //     Log.d("natija auth data", authData.getProviderData().toString());

                            //Set DisplayName
                            if (authData.getProviderData().containsKey("displayName")) {
                                displayName = authData.getProviderData().get("displayName").toString();
                            }

                            //Set Picture
                            downloadImage(authData.getUid(), pictureUrl);

                            //Set Email
                            if (authData.getProviderData().containsKey("email")) {
                                email = authData.getProviderData().get("email").toString();
                            }

                            //Set Birthday & Gender
                            if (authData.getProviderData().containsKey("cachedUserProfile")) {
                                Map<String, Object> cachedUserProfile = (Map<String, Object>) authData.getProviderData().get("cachedUserProfile");
                                if (cachedUserProfile.containsKey("birthday")) {
                                    birthday = Utils.stringToDate(cachedUserProfile.get("birthday").toString());
                                }
                                if (cachedUserProfile.containsKey("gender")) {
                                    gender = cachedUserProfile.get("gender").toString();
                                }
                            }

                            User user = new User(uid, displayName, email, birthday, gender);

                            if (!dataSnapshot.exists()) {

                                //Add User To Firebase
                                ref.child("users").child(authData.getUid()).setValue(user);

                            } else {
                                // Get existing user Id
                                HashMap<String, Object> map = ((HashMap<String, Object>) dataSnapshot.getValue());
                                String id = map.keySet().iterator().next();

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("email", email);
                                userMap.put("birthday", birthday.getTime());
                                userMap.put("gender", gender);
                                ref.child("users").child(id).updateChildren(userMap);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                    Intent mainIntent = new Intent(context,
                            Friends.class);
                    mainIntent.putExtra("id", "1");
                    context.startActivity(mainIntent);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.e("natija", "error " + firebaseError.getMessage());
                }
            });
        } else {
            ref.unauth();
        }
    }

    public void syncFacebookFriends(AccessToken token) {
        Log.d("natija", "hnaaa " + token.toString());
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                token, new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                        ProgressDialog dialog = new ProgressDialog(context);
                        dialog.show();
                        for (int i = 0, size = jsonArray.length(); i < size; i++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String fbid = object.get("id").toString();
                                String uid = "facebook:" + fbid;
                                String name = object.get("name").toString();
                                String imageUrl = ((JSONObject) ((JSONObject) object.get("picture")).get("data")).get("url").toString();
                                Friend friend = new Friend();
                                friend.setDisplayName(name);
                                friend.setuId(uid);
                                downloadImage(uid, imageUrl);
                                ref.child("users").child(ref.getAuth().getUid()).child("friends").child("facebook:" + uid).setValue(friend);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.width(300).height(300)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void downloadImage(String uid, final String url) {
        String dirPath = context.getFilesDir().getAbsolutePath() + File.separator + "pic-profile";

        File file = new File(Environment.getExternalStorageDirectory() + "/" + dirPath + "/" + uid + ".jpg");
        if (file.exists()) {
            file.delete();
        }

        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setDestinationInExternalPublicDir(dirPath, uid + ".jpg")
                .setVisibleInDownloadsUi(false);
        dm.enqueue(request);

    }

    public void theRealDeal() {
        final Firebase refUserRooms = ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms");
        refUserRooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
               /* Iterate User Rooms */
                for (final DataSnapshot snapRoom : snapshot.getChildren()) {

                    final String roomName = snapRoom.getKey();
                    //  checkUnread(roomName);


                    final Firebase refRoomMessages = ref.getRoot().child("messages").child(roomName);

                    refRoomMessages.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            if (!roomMessagesListenerMap.containsKey(s)) {

                                //   if(roomMessagesListenerMap.containsKey(dataSnapshot.getKey()))
                                //   updateRoom(roomName); // Update Last message
                                checkUnread(roomName);


                                if (roomName.equals(currentRoom)) {
                                    updateLastRead(roomName);
                                }
                                roomMessagesListenerMap.put(s, this);

                            }

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
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
    }

    public void updateLastRead(String roomName) {
        Date now = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").child(roomName).setValue(now);
    }

    public void checkUnread(final String roomName) {
        Firebase refUserRooms = ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms");

        refUserRooms.child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("natija list", dataSnapshot.toString());
                Long lastRead = dataSnapshot.getValue(Long.class);
                Query refUnreadChat = ref.getRoot().child("messages").child(roomName).orderByChild("date").startAt(lastRead);

                refUnreadChat.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        Log.d("natija unread count", "last read count " + roomName + " - " + count);
                        unreadMap.put(roomName, count);
                        updateRoom(roomName);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
