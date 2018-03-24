package com.example.henrikfogbunzel.appproject.model;

public class ImagesModel {

    private  String mImageUrl;

    public ImagesModel(){
        //I need a empty constructor
    }

    public ImagesModel(String imageUrl){
        mImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

}
