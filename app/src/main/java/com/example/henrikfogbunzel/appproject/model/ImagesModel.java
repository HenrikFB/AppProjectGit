package com.example.henrikfogbunzel.appproject.model;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImagesModel {

    //private Uri mImageUrl;

    //private UUID uuid;

    //https://stackoverflow.com/questions/41619449/return-all-values-of-latitude-and-longitude-coordinates-from-firebase-database
    //private String lat;
    //private String lng;

    private String imageUriString;
    private String imgUIIDString;
    private String lattitude;
    private String longitude;

    public ImagesModel(String imageUriString, String imgUIIDString, String lattitude, String longitude) {
        this.imageUriString = imageUriString;
        this.imgUIIDString = imgUIIDString;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public ImagesModel(){
        //I need a empty constructor
    }

    public String getImageUriString() {
        return imageUriString;
    }

    public void setImageUriString(String imageUriString) {
        this.imageUriString = imageUriString;
    }

    public String getImgUIIDString() {
        return imgUIIDString;
    }

    public void setImgUIIDString(String imgUIIDString) {
        this.imgUIIDString = imgUIIDString;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /*
    //@Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("URI",  imageUriString);
        result.put("UUID", imgUIIDString);
        result.put("Lat", lattitude);
        result.put("Lon", longitude);

        return result;
    }

    */

}
