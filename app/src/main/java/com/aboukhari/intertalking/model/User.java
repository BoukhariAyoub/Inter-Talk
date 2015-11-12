package com.aboukhari.intertalking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.aboukhari.intertalking.Utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by aboukhari on 23/07/2015.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable(tableName = "user")
public class User implements Parcelable{




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
    String placeId;

    @DatabaseField
    String placeName;

    @DatabaseField
    String gender;

    @DatabaseField
    String imageUrl;

    @DatabaseField
    Boolean isFirstLogin;

    HashMap<String,Integer> languages;
    HashMap<String,Integer> knownLanguages;
    HashMap<String,Integer> wantedLanguages;


    public User() {
    }



    public User(String uid, String displayName, String email, Date birthday, String gender) {
        this.uid = uid;
        this.email = email;
        this.dateAdded = new Date();
        this.birthday = birthday;
        this.displayName = displayName;
        this.gender = gender;
    }

    public HashMap<String, Integer> getLanguages() {
        return languages;
    }

    public void setLanguages(HashMap<String, Integer> languages) {
        this.languages = languages;
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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
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

    @JsonIgnore
    public int getAge() {
        return Utils.getAgeFromDate(this.birthday);
    }

    public Boolean getFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.isFirstLogin = firstLogin;
    }



    public HashMap<String, Integer> getKnownLanguages() {
        return knownLanguages;
    }

    public void setKnownLanguages(HashMap<String, Integer> knownLanguages) {
        this.knownLanguages = knownLanguages;
    }

    public HashMap<String, Integer> getWantedLanguages() {
        return wantedLanguages;
    }

    public void setWantedLanguages(HashMap<String, Integer> wantedLanguages) {
        this.wantedLanguages = wantedLanguages;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", dateAdded=" + dateAdded +
                ", birthday=" + birthday +
                ", displayName='" + displayName + '\'' +
                ", spokenLanguages='" + spokenLanguages + '\'' +
                ", placeId='" + placeId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", gender='" + gender + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.email);
        dest.writeLong(dateAdded != null ? dateAdded.getTime() : -1);
        dest.writeLong(birthday != null ? birthday.getTime() : -1);
        dest.writeString(this.displayName);
        dest.writeString(this.spokenLanguages);
        dest.writeString(this.placeId);
        dest.writeString(this.placeName);
        dest.writeString(this.gender);
        dest.writeString(this.imageUrl);
        dest.writeValue(this.isFirstLogin);
        dest.writeSerializable(this.languages);
        dest.writeSerializable(this.knownLanguages);
        dest.writeSerializable(this.wantedLanguages);
    }

    protected User(Parcel in) {
        this.uid = in.readString();
        this.email = in.readString();
        long tmpDateAdded = in.readLong();
        this.dateAdded = tmpDateAdded == -1 ? null : new Date(tmpDateAdded);
        long tmpBirthday = in.readLong();
        this.birthday = tmpBirthday == -1 ? null : new Date(tmpBirthday);
        this.displayName = in.readString();
        this.spokenLanguages = in.readString();
        this.placeId = in.readString();
        this.placeName = in.readString();
        this.gender = in.readString();
        this.imageUrl = in.readString();
        this.isFirstLogin = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.languages = (HashMap<String, Integer>) in.readSerializable();
        this.knownLanguages = (HashMap<String, Integer>) in.readSerializable();
        this.wantedLanguages = (HashMap<String, Integer>) in.readSerializable();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
