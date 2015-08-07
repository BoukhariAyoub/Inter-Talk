package com.aboukhari.intertalking.model;

/**
 * Created by aboukhari on 07/08/2015.
 */
public class Place {

    String id;
    String description;
    String city;
    String region;
    String country;
    String url;
    int latitude;
    int longtitude;

    public Place(String id, String description, String city, String region, String country) {
        this.id = id;
        this.description = description;
        this.city = city;
        this.region = region;
        this.country = country;
    }

    public Place(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public Place(final String placeId) {




    }

    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(int longtitude) {
        this.longtitude = longtitude;
    }

    @Override
    public String toString() {
        return description;
    }


    public String getInfo() {
        return "Place{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", url='" + url + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longtitude='" + longtitude + '\'' +
                '}';
    }
}
