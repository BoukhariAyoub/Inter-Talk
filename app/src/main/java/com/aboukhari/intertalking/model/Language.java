package com.aboukhari.intertalking.model;

/**
 * Created by aboukhari on 06/08/2015.
 */
public class Language {

    private String id;
    private String name;

    public Language() {
    }

    public Language(String id, String name) {
        this.id = id;
        this.name = name;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
