package com.aboukhari.intertalking.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by aboukhari on 16/07/2015.
 */


@DatabaseTable(tableName = "friend")

public class Friend {

    @DatabaseField(id = true)
    private String uId;

    @DatabaseField
    private String displayName;

    @DatabaseField
    private String spokenLanguages;

    @DatabaseField
    private String city;

    @DatabaseField
    private String country;

    @DatabaseField
    private String gender;

    @DatabaseField
    private int age;

    public Friend() {
    }

    public Friend(String uId, String displayName, String spokenLanguages, String city, String country, String gender, Date birthdate) {
        this.uId = uId;
        this.displayName = displayName;
        this.spokenLanguages = spokenLanguages;
        this.city = city;
        this.country = country;
        this.gender = gender;
        this.age = birthDateToAge(birthdate);
    }

    public Friend(String uId, String displayName) {
        this.uId = uId;
        this.displayName = displayName;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private int birthDateToAge(Date birthdate) {

        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthdate);
        if (dob.after(now)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }
        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);
        int age = year1 - year2;
        int month1 = now.get(Calendar.MONTH);
        int month2 = dob.get(Calendar.MONTH);
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = dob.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }
        return age;
    }
}
