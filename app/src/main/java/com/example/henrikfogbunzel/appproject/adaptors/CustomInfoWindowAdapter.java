package com.example.henrikfogbunzel.appproject.adaptors;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henrikfogbunzel.appproject.R;
import com.example.henrikfogbunzel.appproject.model.ImagesModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

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
        //((ImageView) view.findViewById(R.id.img).setImageResource(mImagesModel.getImageUriString()));

        final String uriFromTitle = marker.getTitle();
        //Picasso.get().load(uriFromTitle).fit().centerCrop().into(img);

        //Uri fileUri = Uri.parse(uriFromTitle);
        //img.setImageURI(fileUri);
        Picasso.get().load(uriFromTitle).fit().centerCrop().into(img);
/*
        String uriString = mImagesModel.getImageUriString();
        Uri fileUri = Uri.parse(uriFromTitle);
        img.setImageURI(fileUri);
*/
        return v;
        //Picasso.get().load(mImagesModel.getImageUriString()).fit().centerCrop().into(img);

        //final String title = marker.getTitle();
    }

    @Override
    public View getInfoContents(Marker marker) {
        //rendowWindowText(marker);
        return null;
    }
}
