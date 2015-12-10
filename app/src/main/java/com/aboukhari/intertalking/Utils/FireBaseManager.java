package com.aboukhari.intertalking.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.activity.ChatRoom;
import com.aboukhari.intertalking.activity.Login;
import com.aboukhari.intertalking.activity.RegistrationActivity;
import com.aboukhari.intertalking.activity.main.Conversations;
import com.aboukhari.intertalking.activity.main.Main3Activity;
import com.aboukhari.intertalking.database.DatabaseManager;
import com.aboukhari.intertalking.model.Conversation;
import com.aboukhari.intertalking.model.Friend;
import com.aboukhari.intertalking.model.Language;
import com.aboukhari.intertalking.model.Message;
import com.aboukhari.intertalking.model.Place;
import com.aboukhari.intertalking.model.User;
import com.aboukhari.intertalking.model.UserRoom;
import com.aboukhari.intertalking.retrofit.RestClient;
import com.aboukhari.intertalking.task.UploadImage;
import com.dd.processbutton.iml.ActionProcessButton;
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
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by aboukhari on 24/07/2015.
 */
public class FireBaseManager {

    Context context;
    private DatabaseManager databaseManager;
    Firebase ref;

    Map<String, ChildEventListener> messagesListenerMap = new HashMap<>();
    public static Map<String, Long> unreadMap = new HashMap();
    public static String currentRoom = null;
    Map<String, ChildEventListener> roomMessagesListenerMap = new HashMap<>();
    private ValueEventListener connectedListener;


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
    public String createRoom(final User friend) {
        //  User user = databaseManager.getCurrentUser(ref.getAuth().getUid());
        final Conversation conversation = new Conversation(ref, ref.getAuth().getUid(), friend.getUid(), "", "");
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
                    ref.child("users").child(friend.getUid()).child("rooms").child(conversation.getRoomName()).setValue(0);

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
                        intent.putExtra("friend", friend);
                        intent.putExtra("roomName", roomName);
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

    /*
    * Add Listener For every room in user's rooms*/
    public void addAllListeners() {
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
                            Log.d("the real deal", "s = " + s);
                            Log.d("the real deal", "dataSnapshot = " + dataSnapshot);
                            Log.d("the real deal", "getKey = " + dataSnapshot.getKey());
                            //if the listener is not already added
                            if (!roomMessagesListenerMap.containsKey(s)) {

                                checkUnread(roomName);

                                if (roomMessagesListenerMap.containsKey(dataSnapshot.getKey())) {
                                    updateRoom(roomName); // Update Last message
                                }


                                if (roomName.equals(currentRoom)) {
                                    updateLastRead(roomName);
                                    updateRoom(roomName);
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
        ref.getRoot().child("users").child(ref.getAuth().getUid()).child("rooms").child(roomName).setValue(ServerValue.TIMESTAMP);
    }

    /* retrieve the count of unread messages */
    public void checkUnread(final String roomName) {
        Log.d("the real deal", "check unread");
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
        Utils.saveUserToPreferences(ctx, null);
        Intent intent = new Intent(ctx, Login.class);
        ctx.startActivity(intent);
    }

    public void addGeneratedUsers() {
        ArrayList<User> users = JsonUtils.generateRandomUsers(context);
        for (User user : users) {
            user.setUid("Random:" + user.getUid());
            Map<String, Object> userMap = Utils.objectToMap(user);
            ref.child("users").child(user.getUid()).updateChildren(userMap);
        }
    }

    public void addUserToFireBase(User user) {
        Map<String, Object> userMap = Utils.objectToMap(user);
        Log.d("natija", user.toString());
        ref.getRoot().child("users").child(user.getUid()).updateChildren(userMap);
    }

    public void resetPassword(String email, final ProgressDialog progressDialog) {
        ref.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(context, "A password has been sent to the email you entred", Toast.LENGTH_LONG).show();
                Log.d("natija pass", "A password has been sent to the email you entred");
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                progressDialog.dismiss();
                Toast.makeText(context, "There Was an error sending an email : " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("natija pass", "There Was an error sending an email : " + firebaseError.getMessage());
            }
        });
    }

    /**
     * Register a new User with email
     *
     * @param email
     */
    public void createUser(final String email, final ActionProcessButton button) {
        String password = Long.toHexString(Double.doubleToLongBits(Math.random()));

        ref.createUser(email.toLowerCase(), password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(final Map<String, Object> result) {

                        ref.getRoot().child("users").orderByChild("email").equalTo(email.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //IF EMAIL ALREADY EXISTS
                                if (!dataSnapshot.exists()) {

                                    String uid = result.get("uid").toString();
                                    User user = new User();
                                    user.setUid(uid);
                                    user.setEmail(email);
                                    user.setFirstLogin(true);
                                    addUserToFireBase(user);


                                    Utils.saveUserToPreferences(context, user);


                                    ref.resetPassword(email, new Firebase.ResultHandler() {
                                        @Override
                                        public void onSuccess() {
                                            button.setProgress(100);
                                            button.setText("Password Sent");
                                            Toast.makeText(context, "A password has been sent to the email you entred", Toast.LENGTH_LONG).show();
                                            Log.d("natija pass", "A password has been sent to the email you entred");
                                        }

                                        @Override
                                        public void onError(FirebaseError firebaseError) {
                                            button.setProgress(-1);
                                            button.setError("Error");
                                            button.setText("Error");
                                            button.setErrorText("Error");
                                            Toast.makeText(context, "There Was an error sending an email : " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.d("natija pass", "There Was an error sending an email : " + firebaseError.getMessage());

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        button.setProgress(-1);
                        button.setError("Error", null);
                        button.setText("Error");
                        button.setErrorText("Error");
                        Toast.makeText(context, "There Was an error sending an email : " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("natija pass", firebaseError.getMessage());
                    }
                }

        );

    }


    /**
     * Update user after Registration and first login
     *
     * @param user
     * @param oldPassword
     * @param newPassword
     * @param bitmap
     * @param placeId
     * @param knownLanguages
     * @param wantedLanguages
     */

    public void updateFirstLoginProfile(final User user, String oldPassword, String newPassword, final Bitmap bitmap, final String placeId, final ArrayList<Language> knownLanguages, final ArrayList<Language> wantedLanguages) {
        String email = user.getEmail();
        ref.changePassword(email, oldPassword, newPassword.trim(), new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                user.setFirstLogin(false);
                addUserToFireBase(user);
                new UploadImage(context, user).execute(bitmap);
                registerPlace(user, placeId);
                registerLanguages(user, knownLanguages, wantedLanguages);
                Utils.saveUserToPreferences(context, user);
                Intent intent = new Intent(context, Login.class);
                context.startActivity(intent);
            }

            @Override
            public void onError(FirebaseError firebaseError) {

            }
        });
    }

    public void updateFirstFacebookLoginProfile(final User user, final Bitmap bitmap, final String placeId, final ArrayList<Language> knownLanguages, final ArrayList<Language> wantedLanguages) {
        user.setFirstLogin(false);
        addUserToFireBase(user);
        if (bitmap != null) {
            new UploadImage(context, user).execute(bitmap);
        }
        registerPlace(user, placeId);
        registerLanguages(user, knownLanguages, wantedLanguages);
        Utils.saveUserToPreferences(context, user);
        Intent intent = new Intent(context, Main3Activity.class);
        context.startActivity(intent);
    }

    private void registerPlace(final User user, String placeId) {
        if (placeId != null) {


            RestClient.get(RestClient.GOOGLE_MAPS_ENDPOINT).getPlaceDetails(placeId, "en", Constants.GOOGLE_API_KEY, new Callback<JsonElement>() {
                @Override
                public void success(JsonElement json, Response response) {
                /*Set Place To User*/
                    Place place = JsonUtils.jSonToPlace(json);
                    FireBaseManager.getInstance(context).addPlaceToUser(place, user);
                }


                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private void registerLanguages(User user, ArrayList<Language> knownLanguages, ArrayList<Language> wantedLanguages) {
        for (Language language : knownLanguages) {
            String languageType = "knownLanguages";
            addLanguageToUser(user.getUid(), languageType, language);
        }

        for (Language language : wantedLanguages) {
            String languageType = "wantedLanguages";
            addLanguageToUser(user.getUid(), languageType, language);
        }
    }

    /**
     * add place to User
     *
     * @param place
     * @param user
     */
    public void addPlaceToUser(Place place, User user) {
        ref.getRoot().child("places").child(place.getId()).setValue(place);
        ref.getRoot().child("users").child(user.getUid()).child("place").setValue(place);
    }

    public void addImageToUser(User user, String imageUrl) {
        ref.getRoot().child("users").child(user.getUid()).child("imageUrl").setValue(imageUrl);
    }

    public void addLanguageToUser(String uid, String languageType, Language language) {
        ref.getRoot().child("users").child(uid).child(languageType).child(language.getIso()).setValue(language.getLevel());
        ref.getRoot().child(languageType).child(language.getIso()).child(uid).setValue(true);
    }

    public void deleteLanguageFromUser(String uid, String languageType, Language language) {
        ref.getRoot().child("users").child(uid).child(languageType).child(language.getIso()).removeValue();
        ref.getRoot().child(languageType).child(language.getIso()).child(uid).removeValue();
    }


    public void onFacebookAccessTokenChange(final AccessToken token, final String pictureUrl, final ActionProcessButton button) {
        if (token != null) {
            ref.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(final AuthData authData) {

                    String email = authData.getProviderData().get("email").toString().toLowerCase();

                    ref.getRoot().child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Log.d("natija fb", "user already exists");
                                DataSnapshot dataUser = dataSnapshot.getChildren().iterator().next();
                                User user = dataUser.getValue(User.class);
                                Log.d("natija fb", "if user " + user.toString());
                                loginExistingUser(user, true);
                            }

                            //user does not exists
                            else {
                                Log.d("natija fb", "user does not exists");
                                String uid = authData.getUid();
                                String email = "";
                                Date birthday = new Date(0L);
                                String displayName = "";
                                String gender = "";


                                //Set DisplayName
                                if (authData.getProviderData().containsKey("displayName")) {
                                    displayName = authData.getProviderData().get("displayName").toString();
                                }

                                //Set Email
                                if (authData.getProviderData().containsKey("email")) {
                                    email = authData.getProviderData().get("email").toString().toLowerCase();
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
                                user.setFirstLogin(true);

                                //Add User To Firebase
                                addUserToFireBase(user);

                                button.setProgress(100);

                                loginExistingUser(user, true);

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

    /**
     * Login An Existing User
     *
     * @param user
     */
    private void loginExistingUser(User user, boolean facebook) {
        Utils.saveUserToPreferences(context, user);

        Log.d("natija fb", "login existing : " + user);
        if (user.getFirstLogin()) {
            Intent mainIntent = new Intent(context,
                    RegistrationActivity.class);
            mainIntent.putExtra("facebook", facebook);
            context.startActivity(mainIntent);
        } else {
            Utils.saveUserToPreferences(context, user);
            Intent mainIntent = new Intent(context,
                    Main3Activity.class);
            context.startActivity(mainIntent);
        }


    }


    public void loginUserWithPassword(final String email, final String password,final ActionProcessButton button) {

        ref.authWithPassword(email.trim().toLowerCase(), password.trim(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.d("natija auth", " auth data = " + authData);
                final Boolean isTemporaryPassword = (Boolean) authData.getProviderData().get("isTemporaryPassword");
                final String uid = authData.getAuth().get("uid").toString();


                //TODO check if first login
                if (isTemporaryPassword) {

                    User user =new User(uid,null,email,null,null);
                    user.setFirstLogin(true);
                    Utils.saveUserToPreferences(context, user);
                    Intent intent = new Intent(context, RegistrationActivity.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    context.startActivity(intent);
                } else {
                    //TODO getCurrentUser and Update Shared Preferences

                    ref.getRoot().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){
                                User user = dataSnapshot.getValue(User.class);

                                if (user.getFirstLogin()) {
                                    Utils.saveUserToPreferences(context, user);
                                    Intent intent = new Intent(context, RegistrationActivity.class);
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("email", email);
                                    intent.putExtra("password", password);
                                    context.startActivity(intent);
                                } else {
                                    Utils.saveUserToPreferences(context, user);
                                    Intent intent = new Intent(context, Main3Activity.class);
                                    context.startActivity(intent);
                                }

                            }

                            else{

                            }

                            button.setProgress(100);
                        }


                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            button.setProgress(-1);

                        }
                    });

                }

            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                button.setProgress(-1);
                Log.e("natija", firebaseError.getMessage());
            }
        });
    }


    /**
     * Listener That update current Local Database On firebase change
     */
    public void addCurrentUserChangeListener() {
        String uid = ref.getAuth().getUid();
        ref.getRoot().child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Utils.saveUserToPreferences(context, user);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void addFriend(User friend) {
        String uid = ref.getAuth().getUid();
        ref.child("users").child(uid).child("friends").child(friend.getUid()).setValue(true);
        ref.child("users").child(friend.getUid()).child("friends").child(uid).setValue(false);
    }


    public void acceptFriend(User friend) {

    }

    public void updateOnlineStatus() {
        //TODO check if already in preference
        if (Utils.getUserFromPreferences(context) != null) {


            final Firebase isOnline = ref.getRoot().child("users").child(Utils.getUserFromPreferences(context).getUid()).child("online");
            final Firebase lastOnline = ref.getRoot().child("users").child(Utils.getUserFromPreferences(context).getUid()).child("online");
            if (connectedListener == null) {
                connectedListener = ref.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean connected = (Boolean) dataSnapshot.getValue();
                        if (connected) {
                            Log.d("natija online", "is connected ? " + connected);
                            lastOnline.onDisconnect().setValue(ServerValue.TIMESTAMP);
                            isOnline.setValue(true);
                            isOnline.onDisconnect().setValue(false);

                        }

                    }


                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.err.println("Listener was cancelled at .info/connected");
                    }
                });
            }
        }

    }
}
