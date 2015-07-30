package com.aboukhari.intertalking.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@DatabaseTable(tableName = "message")

public class Message {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String message;

    @DatabaseField
    private String author;

    @DatabaseField
    private Date date;

    @SuppressWarnings("unused")
    private Message() {}

    public Message(String message, String author) {
        this.message = message;
        this.author = author;
        this.date = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();

    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", date=" + date +
                '}';
    }
}