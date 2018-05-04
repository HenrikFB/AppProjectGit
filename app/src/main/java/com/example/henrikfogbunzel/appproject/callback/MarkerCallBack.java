package com.example.henrikfogbunzel.appproject.callback;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import com.squareup.picasso.Callback;

public class MarkerCallBack implements Callback {

    //REF: https://stackoverflow.com/questions/32725753/picasso-image-loading-issue-in-googlemap-infowindowadapter

    Marker marker = null;
    String uriFromTitle;
    ImageView img;

    public MarkerCallBack(Marker marker, String uriFromTitle, ImageView img) {
        this.marker = marker;
        this.uriFromTitle = uriFromTitle;
        this.img = img;
    }


    @Override
    public void onSuccess() {
        if (marker != null && marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
            Picasso.get().load(uriFromTitle).into(img);

            marker.showInfoWindow();
        }
    }

        @Override
        public void onError (Exception e){
            Log.e(getClass().getSimpleName(), "MarkerCallBack error with loading");
        }
}
