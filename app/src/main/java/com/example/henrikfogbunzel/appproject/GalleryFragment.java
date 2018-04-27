package com.example.henrikfogbunzel.appproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.henrikfogbunzel.appproject.adaptors.ImageAdapter;
import com.example.henrikfogbunzel.appproject.model.ImagesModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GalleryFragment extends Fragment  implements ImageAdapter.OnItemClickListener{

    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth auth;
    private ValueEventListener mDBListener;

    private List<ImagesModel> mImagesModels;

    private static final String TAG = "CalleryFragment";

    String imgUIIDString;
    String latValue;
    String lngValue;
    Boolean flag;



    OnMessageSendListener messageSendListener;
    public interface OnMessageSendListener
    {
        public void onMeassageSend(String latValue, String lngValue, Boolean flag);
    }

    public GalleryFragment(){
        //empty constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try{
            messageSendListener = (OnMessageSendListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement onMessageSend...");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mRecyclerView = view.findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mImagesModels = new ArrayList<>();

        mImageAdapter = new ImageAdapter(getContext(), mImagesModels);
        mRecyclerView.setAdapter(mImageAdapter);

        mImageAdapter.setOnItemClickListener(GalleryFragment.this);


        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();

        //UUID imgUUID = UUID.randomUUID();
        //final String imgUIIDString = imgUUID.toString();

        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + userID + "/");

        mDBListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mImagesModels.clear();

                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    ImagesModel imagesModel = postSnapShot.getValue(ImagesModel.class);
                    imagesModel.setKey(postSnapShot.getKey());
                    mImagesModels.add(imagesModel);
                }

                mImageAdapter.notifyDataSetChanged();
                //mImageAdapter = new ImageAdapter(getContext(), mImagesModels);
                //mRecyclerView.setAdapter(mImageAdapter);

                //mImageAdapter.setOnItemClickListener(GalleryFragment.this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        return view;
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(), "Normal click at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        //Toast.makeText(getContext(), "onWhatEver " + position, Toast.LENGTH_SHORT).show();
        ImagesModel selectedItem = mImagesModels.get(position);
         latValue = selectedItem.getLattitude();
         lngValue = selectedItem.getLongitude();
         Toast.makeText(getContext(), "onWhatEver " + position + " " + latValue + " " + lngValue, Toast.LENGTH_SHORT).show();
         flag = true;
         messageSendListener.onMeassageSend(latValue, lngValue, flag);
    }

    @Override
    public void onDeleteClick(int position) {
        Toast.makeText(getContext(), "Delete click at position " + position, Toast.LENGTH_SHORT).show();
        ImagesModel selectedItem = mImagesModels.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUriString());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseReference.child(selectedKey).removeValue();
                Toast.makeText(getContext(), "Item deleted " , Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mDatabaseReference.removeEventListener(mDBListener);
    }
}
