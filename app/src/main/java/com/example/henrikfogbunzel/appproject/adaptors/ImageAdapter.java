package com.example.henrikfogbunzel.appproject.adaptors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.henrikfogbunzel.appproject.GalleryFragment;
import com.example.henrikfogbunzel.appproject.R;
import com.example.henrikfogbunzel.appproject.model.ImagesModel;
import com.example.henrikfogbunzel.appproject.model.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    /* REF:
    https://www.youtube.com/watch?v=0B471wsJ_RY&list=PLrnPJCHvNZuB_7nB5QD-4bNg6tpdEUImQ&index=5
    DataStorageDemo
     */

    private Context mContext;
    //private List<ImagesModel> mUploads;
    private List<ImagesModel> mUploads;

    public ImageAdapter(Context context, List<ImagesModel> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        ImagesModel imagesModelCurrent = mUploads.get(position);
        //Upload upload = mUploads.get(position);

        //Picasso.get().load(imagesModelCurrent.getImageUrl()).fit().centerCrop().into(holder.imageView);
        Picasso.get().load(imagesModelCurrent.getImageUriString()).fit().centerCrop().into(holder.imageView);
        //Picasso.get().load(upload.getImageUriString()).fit().centerCrop().into(holder.imageView);
    }
    //DataStorageDemo
    @Override
    public int getItemCount() {
        if(mUploads == null) {
            return 0;
        }
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
