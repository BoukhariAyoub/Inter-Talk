package com.aboukhari.intertalking.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by aboukhari on 15/07/2015.
 */


@DatabaseTable(tableName = "user_room")

public class UserRoom {

    @DatabaseField(id = true)
    private String roomName;

    @DatabaseField
    private Date lastSeen;

    @DatabaseField(foreign = true,columnName = "friend_id")
    private User friend;

    public UserRoom() {
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

}
