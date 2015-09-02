package com.aboukhari.intertalking.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aboukhari.intertalking.model.Conversation;
import com.aboukhari.intertalking.model.Friend;
import com.aboukhari.intertalking.model.TranslatedMessage;
import com.aboukhari.intertalking.model.User;
import com.aboukhari.intertalking.model.UserRoom;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by erazafimampiandra on 08/04/2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private Dao<User, String> mDAOUser = null;
    private Dao<Friend,String> mDAOFriend= null;
    private Dao<UserRoom, String> mDAOUserRoom = null;
    private Dao<Conversation, String> mDAOConversation = null;
    private Dao<TranslatedMessage,String> mDAOMessage = null;


    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Conversation.class);
            TableUtils.createTable(connectionSource, Friend.class);
            TableUtils.createTable(connectionSource, UserRoom.class);
            TableUtils.createTable(connectionSource, TranslatedMessage.class);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Conversation.class, true);
            TableUtils.dropTable(connectionSource, Friend.class, true);
            TableUtils.dropTable(connectionSource, UserRoom.class, true);
            TableUtils.dropTable(connectionSource, TranslatedMessage.class, true);

            onCreate(database, connectionSource);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }






    public Dao<UserRoom, String> getmDaoUserRoom() {
        if(mDAOUserRoom == null) {
            try {
                mDAOUserRoom = getDao(UserRoom.class);
            } catch (SQLException e) {
                Log.e("Sql Error", e.getMessage());
            }
        }
        return mDAOUserRoom;
    }

    public Dao<User, String> getmDaoUser() {
        if(mDAOUser == null) {
            try {
                mDAOUser = getDao(User.class);
            } catch (SQLException e) {
                Log.e("Sql Error", e.getMessage());
            }
        }
        return mDAOUser;
    }

    public Dao<Friend, String> getmDaoFriend() {
        if(mDAOFriend == null) {
            try {
                mDAOFriend = getDao(Friend.class);
            } catch (SQLException e) {
                Log.e("Sql Error", e.getMessage());
            }
        }
        return mDAOFriend;
    }

    public Dao<Conversation, String> getmDaoConversation() {
        if(mDAOConversation == null) {
            try {
                mDAOConversation = getDao(Conversation.class);
            } catch (SQLException e) {
                Log.e("Sql Error", e.getMessage());
            }
        }
        return mDAOConversation;
    }


    public Dao<TranslatedMessage, String> getmDaoMessage() {
        if(mDAOMessage == null) {
            try {
                mDAOMessage = getDao(TranslatedMessage.class);
            } catch (SQLException e) {
                Log.e("Sql Error", e.getMessage());
            }
        }
        return mDAOMessage;
    }




}
