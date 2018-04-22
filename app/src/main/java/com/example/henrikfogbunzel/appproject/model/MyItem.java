package com.example.henrikfogbunzel.appproject.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem{

    //https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MyItem(LatLng position, String title, String snippet) {
        mPosition = position;
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
