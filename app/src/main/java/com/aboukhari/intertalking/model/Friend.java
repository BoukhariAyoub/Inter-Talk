package com.aboukhari.intertalking.model;

/**
 * Created by aboukhari on 16/07/2015.
 */
public class Friend {

    private String uId;
    private String imageUrl;
    private String displayName;

    public Friend(){}

    public Friend(String uId,String displayName,String imageUrl) {
        this.uId = uId;
        this.imageUrl = imageUrl;
        this.displayName = displayName;
    }


    public String getuId() {
        return uId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "uId='" + uId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
