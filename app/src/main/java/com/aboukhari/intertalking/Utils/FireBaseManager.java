package com.aboukhari.intertalking.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.activity.ChatRoom;
import com.aboukhari.intertalking.activity.Login;
import com.aboukhari.intertalking.activity.main.Conversations;
import com.aboukhari.intertalking.activity.main.MainActivity;
import com.aboukhari.intertalking.database.DatabaseManager;
import com.aboukhari.intertalking.model.Conversation;
import com.aboukhari.intertalking.model.Friend;
import com.aboukhari.intertalking.model.Message;
import com.aboukhari.intertalking.model.Place;
import com.aboukhari.intertalking.model.User;
import com.aboukhari.intertalking.model.UserRoom;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
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

import java.util.ArrayList;
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

    public static FireBaseManager getInstance(Context ctx) {
        return new FireBaseManager(ctx);
    }

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
        //  User user = databaseManager.getCurrentUser(ref.getAuth().getUid());
        final Conversation conversation = new Conversation(ref, ref.getAuth().getUid(), friend.getuId(), "", "");
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
                    ref.child("users").child(ref.getAuth().getUid()).child("rooms").child(conversation.getRoomName()).setValue(lastSeen);
                    UserRoom userRoom = new UserRoom();
                    userRoom.setLastSeen(lastSeen);
                    userRoom.setFriend(friend);
                    userRoom.setRoomName(conversation.getRoomName());


                    //Add Room to friend's Rooms
                    ref.child("users").child(friend.getuId()).child("rooms").child(conversation.getRoomName()).setValue(0);

                }

                openRoom(conversation.getRoomName());

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


    /**
     * @param roomName
     */
    public void openRoom(final String roomName) {
        ref.getRoot().child("room_names").child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Conversation conversation = dataSnapshot.getValue(Conversation.class);
                String friendUid = conversation.extractFriendUid(ref.getRoot());
                ref.getRoot().child("users").child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User friend = dataSnapshot.getValue(User.class);
                        Intent intent = new Intent(context, ChatRoom.class);
                        intent.putExtra("title", friend.getDisplayName());
                        intent.putExtra("roomName", roomName);
                        intent.putExtra("imageUrl", friend.getImageUrl());
                        context.startActivity(intent);
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

    /**
     * @param title
     * @param text
     */
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


    public void onFacebookAccessTokenChange(final Context context, final AccessToken token, final String pictureUrl) {
        if (token != null) {
            ref.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(final AuthData authData) {

                    String email = authData.getProviderData().get("email").toString();

                    ref.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //if user already exists
                            if (dataSnapshot.exists()) {

                                DataSnapshot dataUser = dataSnapshot.getChildren().iterator().next();

                                User user = dataUser.getValue(User.class);
                                Utils.saveUserToPreferences(context, user);

                                //TODO login()
                                Intent mainIntent = new Intent(context,
                                        MainActivity.class);
                                context.startActivity(mainIntent);


                            }

                            //user does not exists
                            else {
                                String uid = authData.getUid();
                                String email = "";
                                Date birthday = new Date(0L);
                                String displayName = "";
                                String gender = "";


                                //Set DisplayName
                                if (authData.getProviderData().containsKey("displayName")) {
                                    displayName = authData.getProviderData().get("displayName").toString();
                                }

                                //Set Picture //TODO
                                //  downloadImage(authData.getUid(), pictureUrl);


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
                                user.setImageUrl(pictureUrl);
                                //TODO REMOVE
                                HashMap<String, Integer> langs = new HashMap<>();
                                langs.put("en", 0);
                                langs.put("fr", 3);
                                langs.put("ar", 2);


                                user.setLanguages(langs);
                                //Add User To Firebase
                                addNewUserToFireBase(user);


                                Intent mainIntent = new Intent(context,
                                        MainActivity.class);
                                mainIntent.putExtra("id", "1");
                                context.startActivity(mainIntent);

                                Log.d("natija pref", "2nd gateway");
                                Utils.saveUserToPreferences(context, user);


                               /* Intent intent = new Intent(context, SpringIndicator.class);
                                intent.putExtra("user", user);
                                Log.d("natija user", "from fb" + user);
                                context.startActivity(intent);*/
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


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
                                //  friend.setDisplayName(name);
                                friend.setuId(uid);
                                ref.child("users").child(ref.getAuth().getUid()).child("friends").child(uid).setValue(friend);
                                dialog.dismiss();
                            } catch (JSONException e) {
                                dialog.dismiss();
                                Log.e("natija ", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.width(400).height(400)");
        request.setParameters(parameters);
        request.executeAsync();
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

                            //if the listener is not already added
                            if (!roomMessagesListenerMap.containsKey(s)) {

                                if (roomMessagesListenerMap.containsKey(dataSnapshot.getKey())) {
                                    updateRoom(roomName); // Update Last message
                                }
                                checkUnread(roomName);


                                if (roomName.equals(currentRoom)) {
                                    updateLastRead(roomName);
                                } else {
                                    updateRoom(roomName);
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


    /* retrieve the count of unread messages */
    public void checkUnread(final String roomName) {
        Firebase refUserRooms = ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms");

        refUserRooms.child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long lastRead = dataSnapshot.getValue(Long.class);
                Query refUnreadChat = ref.getRoot().child("messages").child(roomName).orderByChild("date").startAt(lastRead);

                refUnreadChat.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        unreadMap.put(roomName, count);
                        updateRoom(roomName);
                        Conversations.conversationsRecyclerAdapter.notifyDataSetChanged();
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

    public void logout(Context ctx) {
        ref.unauth();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(ctx, Login.class);
        ctx.startActivity(intent);

    }


    public void addGeneratedUsers() {
        ArrayList<User> users = Utils.generateRandomUsers(context);
        for (User user : users) {
            Map<String, Object> userMap = Utils.objectToMap(user);
            ref.child("users").child(user.getUid()).updateChildren(userMap);
        }
    }

    /**
     * Add Place To DataBase
     *
     * @param place
     */
    public void addPlace(Place place) {
        ref.getRoot().child("places").child(place.getId()).setValue(place);
    }

    public void addLanguages(User user) {
        //  ref.getRoot().child("languages").child(place.getId()).setValue(place);
        HashMap<String, Integer> langs = user.getLanguages();

        //   for(String iso : map.St)

    }


    public void addUserToFireBase(User user) {
        Map<String, Object> userMap = Utils.objectToMap(user);
        ref.child("users").child(user.getUid()).updateChildren(userMap);
    }

    private void addNewUserToFireBase(User user) {
        Map<String, Object> userMap = Utils.objectToMap(user);
        ref.child("users").child(user.getUid()).updateChildren(userMap);
    }

    public void addImageToUser(String uid, String imageUrl) {
        ref.child("users").child(uid).child("imageUrl").setValue(imageUrl);
    }


}
