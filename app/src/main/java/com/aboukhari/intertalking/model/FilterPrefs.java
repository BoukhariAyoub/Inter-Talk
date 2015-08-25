package com.aboukhari.intertalking.model;

import java.util.ArrayList;

/**
 * Created by aboukhari on 25/08/2015.
 */
public class FilterPrefs {

    String gender;
    Integer ageMax;
    Integer ageMin;
    Boolean isOnline;
    Boolean hasPicture;
    ArrayList<String> languages;
    Place place;


    public FilterPrefs() {
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Boolean getHasPicture() {
        return hasPicture;
    }

    public void setHasPicture(Boolean hasPicture) {
        this.hasPicture = hasPicture;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<String> languages) {
        this.languages = languages;
    }

    @Override
    public String toString() {
        return "FilterPrefs{" +
                "gender='" + gender + '\'' +
                ", ageMax=" + ageMax +
                ", ageMin=" + ageMin +
                ", isOnline=" + isOnline +
                ", hasPicture=" + hasPicture +
                ", languages=" + languages +
                ", place=" + place +
                '}';
    }
}
