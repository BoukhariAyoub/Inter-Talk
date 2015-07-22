package com.aboukhari.intertalking.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by aboukhari on 15/07/2015.
 */
public class Room {

    private String name;
    private Date date;

    public Room() {
    }

    public Room(String name) {
        this.name = name;
        this.date = Calendar.getInstance().getTime();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

}
