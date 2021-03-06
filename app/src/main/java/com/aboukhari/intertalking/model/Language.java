package com.aboukhari.intertalking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Locale;

/**
 * Created by aboukhari on 06/08/2015.
 */
public class Language {

    public static final int LEVEL_BEGINNER = 0;
    public static final int LEVEL_INTERMEDIATE = 1;
    public static final int LEVEL_ADVANCED = 2;
    public static final int LEVEL_NATIVE = 3;


    String iso;
    int level;

    public Language(String iso) {
        this.iso = iso;
        level = LEVEL_BEGINNER;
    }

    public Language(String iso, int level) {
        this.iso = iso;
        this.level = level;
    }

    public String getIso() {
        return iso;
    }

    @JsonIgnore
    public String getName() {
        return new Locale(iso).getDisplayLanguage().toUpperCase();

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void updateLevel() {
        if (this.level < 3) {
            this.level++;
            return;
        }
        level = 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Language)) return false;
        return iso.equalsIgnoreCase(((Language) o).getIso());
    }

    @Override
    public int hashCode() {
        return(iso.hashCode());
    }
}
