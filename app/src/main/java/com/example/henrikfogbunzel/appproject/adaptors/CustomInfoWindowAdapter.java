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

    private final View mWindow;
    private Context mContext;

    ImagesModel mImagesModel;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void rendowWindowText(Marker marker, View view){
        ImageView img = (ImageView) view.findViewById(R.id.img);
        //((ImageView) view.findViewById(R.id.img).setImageResource(mImagesModel.getImageUriString()));

        //final String uriFromTitle = marker.getTitle();
        //Picasso.get().load(uriFromTitle).fit().centerCrop().into(img);
        String uriFromTitle = marker.getTitle();

        //Uri fileUri = Uri.parse(uriFromTitle);
        //img.setImageURI(fileUri);
/*
        String uriString = mImagesModel.getImageUriString();
        Uri fileUri = Uri.parse(uriFromTitle);
        img.setImageURI(fileUri);
*/

        //Picasso.get().load(mImagesModel.getImageUriString()).fit().centerCrop().into(img);

        //final String title = marker.getTitle();
    }

    /*

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        ImagesModel imagesModelCurrent = mUploads.get(position);
        //Upload upload = mUploads.get(position);

        //Picasso.get().load(imagesModelCurrent.getImageUrl()).fit().centerCrop().into(holder.imageView);
        Picasso.get().load(imagesModelCurrent.getImageUriString()).fit().centerCrop().into(holder.imageView);
        //Picasso.get().load(upload.getImageUriString()).fit().centerCrop().into(holder.imageView);
    }
     */

    @Override
    public View getInfoWindow(Marker marker) {
        //https://stackoverflow.com/questions/42885480/display-image-in-infowindow-from-url-google-maps-android-api
        rendowWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }
}
