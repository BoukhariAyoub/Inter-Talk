package com.aboukhari.intertalking.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Chat {

    private String message;
    private String author;
    private Date date;

    @SuppressWarnings("unused")
    private Chat() {}

    public Chat(String message, String author) {
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
}