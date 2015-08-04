package com.aboukhari.intertalking.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by aboukhari on 23/07/2015.
 */


@DatabaseTable(tableName = "user")

public class User {

    @DatabaseField(id = true)
    String uid;

    @DatabaseField
    String email;

    @DatabaseField
    Date dateAdded;

    @DatabaseField
    Date birthday;

    @DatabaseField
    String displayName;

    @DatabaseField
    String spokenLanguages;

    @DatabaseField
    String city;

    @DatabaseField
    String country;

    @DatabaseField
    String gender;

    @DatabaseField
    String imageUrl;

    public User() {
    }

    public User(String uid,String displayName, String email, Date birthday, String gender) {
        this.uid = uid;
        this.email = email;
        this.dateAdded = new Date();
        this.birthday = birthday;
        this.displayName = displayName;
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(String spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", dateAdded=" + dateAdded +
                ", birthday=" + birthday +
                ", displayName='" + displayName + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
