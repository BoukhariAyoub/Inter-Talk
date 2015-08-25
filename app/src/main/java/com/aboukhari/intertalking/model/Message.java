package com.aboukhari.intertalking.model;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.firebase.client.ServerValue;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Map;

@DatabaseTable(tableName = "message")

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String message;

    @DatabaseField
    private String author;

    @JsonIgnore
    Long date;

    @SuppressWarnings("unused")
    private Message() {
    }

    public Message(String message, String author) {
        this.message = message;
        this.author = author;
        Log.d("natija date", ServerValue.TIMESTAMP.toString());

    }

    public String getMessage() {
        return message;

    }

    public String getAuthor() {
        return author;
    }

    @JsonIgnore
    public Date getDate() {
        return new Date(date);
    }

    @Override
    public String toString() {
        return "Chat{" +
                "message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", date=" + getDate() +
                '}';
    }


    @JsonProperty("date")
    public Map<String, String> getTime() {
        return ServerValue.TIMESTAMP;
    }

    @JsonProperty("date")
    public void setTime(Long time) {
        this.date = time;
    }
}