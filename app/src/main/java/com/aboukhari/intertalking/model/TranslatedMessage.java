package com.aboukhari.intertalking.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by aboukhari on 02/09/2015.
 */


@DatabaseTable(tableName = "translated_messages")
public class TranslatedMessage {

    @DatabaseField(id = true)
    String id;

    @DatabaseField
    String text;

    @DatabaseField
    String lang;

    /*unused*/
    public TranslatedMessage() {
    }

    public TranslatedMessage(String id, String text, String lang) {
        this.id = id;
        this.text = text;
        this.lang = lang;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


}
