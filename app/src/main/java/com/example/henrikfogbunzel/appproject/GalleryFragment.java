package com.example.henrikfogbunzel.appproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.henrikfogbunzel.appproject.adaptors.ImageAdapter;
import com.example.henrikfogbunzel.appproject.model.ImagesModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GalleryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth auth;

    private List<ImagesModel> mImagesModels;

    private static final String TAG = "CalleryFragment";


    String imgUIIDString;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mRecyclerView = view.findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mImagesModels = new ArrayList<>();


        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();

        //UUID imgUUID = UUID.randomUUID();
        //final String imgUIIDString = imgUUID.toString();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + userID + "/" + "/"+imgUIIDString+"/" + imgUIIDString);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    ImagesModel imagesModel = postSnapShot.getValue(ImagesModel.class);
                    mImagesModels.add(imagesModel);
                }
                mImageAdapter = new ImageAdapter(getContext(), mImagesModels);
                mRecyclerView.setAdapter(mImageAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        return view;
    }
}
