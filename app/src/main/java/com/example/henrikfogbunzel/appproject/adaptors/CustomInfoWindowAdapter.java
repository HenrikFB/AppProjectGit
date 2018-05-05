package com.example.henrikfogbunzel.appproject.adaptors;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henrikfogbunzel.appproject.R;
import com.example.henrikfogbunzel.appproject.callback.MarkerCallBack;
import com.example.henrikfogbunzel.appproject.model.ImagesModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    /*
    google searh:picasso InfoWindowAdapter
    for fikse dobbelt klik:
    https://stackoverflow.com/questions/32725753/picasso-image-loading-issue-in-googlemap-infowindowadapter

     */


    //private final View mWindow;
    private Context mContext;
    private LayoutInflater inflater;


    ImagesModel mImagesModel;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        //mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = inflater.inflate(R.layout.custom_info_window, null);
        ImageView img = (ImageView) v.findViewById(R.id.img);

        final String uriFromTitle = marker.getTitle();

        //Picasso.get().load(uriFromTitle).into(img);
        Picasso.get().load(uriFromTitle).into(img, new MarkerCallBack(marker,uriFromTitle, img));
        Log.d("adf", ""+img.getHeight());


        return v;

    }

    @Override
    public View getInfoContents(Marker marker) {
        //rendowWindowText(marker);
        return null;
    }
}
