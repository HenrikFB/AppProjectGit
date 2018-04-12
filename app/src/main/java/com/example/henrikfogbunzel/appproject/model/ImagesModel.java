package com.example.henrikfogbunzel.appproject.model;

public class ImagesModel {

    private  String mImageUrl;

    private int uuid;

    public ImagesModel(){
        //I need a empty constructor
    }

    public ImagesModel(String imageUrl, int uuid){
        mImageUrl = imageUrl;
        this.uuid = uuid;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }
}
