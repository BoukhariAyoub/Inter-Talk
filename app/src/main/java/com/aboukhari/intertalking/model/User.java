package com.aboukhari.intertalking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by aboukhari on 23/07/2015.
 */


@DatabaseTable(tableName = "user")
public class User implements Parcelable {



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
                ", spokenLanguages='" + spokenLanguages + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
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
        dest.writeString(this.city);
        dest.writeString(this.country);
        dest.writeString(this.gender);
        dest.writeString(this.imageUrl);
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
        this.city = in.readString();
        this.country = in.readString();
        this.gender = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
