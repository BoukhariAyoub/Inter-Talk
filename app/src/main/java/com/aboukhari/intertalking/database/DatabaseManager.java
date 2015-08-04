package com.aboukhari.intertalking.database;

import android.content.Context;
import android.util.Log;

import com.aboukhari.intertalking.model.Conversation;
import com.aboukhari.intertalking.model.Friend;
import com.aboukhari.intertalking.model.Message;
import com.aboukhari.intertalking.model.User;
import com.aboukhari.intertalking.model.UserRoom;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by erazafimampiandra on 21/04/2015.
 */
public class DatabaseManager {
    static private DatabaseManager instance;

    static private String DATABASE_NAME = "intertalk";
    static private int DATABASE_VERSION = 1;


    static public DatabaseManager getInstance(Context ctx) {
        if (null == instance) {
            instance = new DatabaseManager(ctx);
        }
        return instance;
    }

    private DatabaseHelper helper;

    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

     /*Create*/


    public void addUser(User user) {
        try {
            getHelper().getmDaoUser().create(user);
        } catch (SQLException e) {
            Log.e("Sql Error", e.getMessage());
        }
    }

    public void addFriend(Friend friend) {
        try {
            getHelper().getmDaoFriend().create(friend);
        } catch (SQLException e) {
            Log.e("Sql Error", e.getMessage());
        }
    }

    public void addConversation(Conversation conversation) {
        try {
            getHelper().getmDaoConversation().create(conversation);
        } catch (SQLException e) {
            Log.e("Sql Error", e.getMessage());
        }
    }

    public void addMessage(Message message) {
        try {
            getHelper().getmDaoMessage().create(message);
        } catch (SQLException e) {
            Log.e("Sql Error", e.getMessage());
        }
    }

    public void addUserRoom(UserRoom userRoom) {
        try {
            getHelper().getmDaoUserRoom().createOrUpdate(userRoom);
        } catch (SQLException e) {
            Log.e("Sql Error", e.getMessage());
        }
    }

    /*Read*/
    public Long getLastSeen(String roomName) {
        UserRoom userRoom;
        try {
            userRoom = getHelper().getmDaoUserRoom().queryForId(roomName);
            if (userRoom != null)
                return userRoom.getLastSeen().getTime();
        } catch (SQLException e) {
            Log.e("Sql Error", e.getMessage());
        }
        return 0L;

    }

    public User getCurrentUser(String uId) {
        User user;
        try {
            user = getHelper().getmDaoUser().queryForId(uId);
            if (user != null) {
                return user;
            }
        } catch (SQLException e) {
            Log.e("Sql Error", e.getMessage());
        }
        return null;
    }

    /*Update*/
    public void updateLastSeen(String roomName, Long lastSeen) {
        UpdateBuilder<UserRoom, String> updateBuilder = getHelper().getmDaoUserRoom().updateBuilder();
        try {
            updateBuilder.where().eq("roomName", roomName);

            updateBuilder.updateColumnValue("lastSeen", new Date(lastSeen));
            Log.d("natija unread", "updateLastSeen " + lastSeen);

            int what = updateBuilder.update();

            Log.d("natija unread", "what what what what " + what);
        } catch (SQLException e) {
            Log.d("Sql Error", e.getMessage());
        }
    }


}


