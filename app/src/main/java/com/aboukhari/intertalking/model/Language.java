package com.aboukhari.intertalking.model;

/**
 * Created by aboukhari on 06/08/2015.
 */
public class Language {


    String iso;
    String name;

    public Language(String iso, String name) {
        this.iso = iso;
        this.name = name;
    }

    public String getIso() {
        return iso;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
       return name;
    }
}
