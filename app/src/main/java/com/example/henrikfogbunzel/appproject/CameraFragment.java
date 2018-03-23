package com.example.henrikfogbunzel.appproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment {

    Button cameraBtnUpload, cameraBtnCapture;
    ImageView cameraImageView;

    private StorageReference mStorageReference;
    private FirebaseAuth auth;

    private static final int CAMERA_REQUEST_CODE = 1;

    //private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        cameraBtnUpload = (Button) view.findViewById(R.id.cameraBtnUpload);
        cameraBtnCapture = (Button) view.findViewById(R.id.cameraBtnCapture);
        cameraImageView = (ImageView) view.findViewById(R.id.cameraImageView);

        //mProgressDialog = new ProgressDialog(getContext());

        auth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        cameraBtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                //get the signed in user
                //FirebaseUser user = auth.getCurrentUser();
                //String userID = user.getUid();
            }
        });

        cameraBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get the signed in user
                //FirebaseUser user = auth.getCurrentUser();
                //String userID = user.getUid();

                //Uri downloadUri = taskSnapshot.getDownloadUrl();

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK ){

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            cameraImageView.setImageBitmap(imageBitmap);

            cameraImageView.setDrawingCacheEnabled(true);
            cameraImageView.buildDrawingCache();
            Bitmap bitmap = cameraImageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataByte = baos.toByteArray();


            FirebaseUser user = auth.getCurrentUser();
            String userID = user.getUid();

            StorageReference storageReference = mStorageReference.child("images/users/" + userID);

            UploadTask uploadTask = storageReference.putBytes(dataByte);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Upload failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Toast.makeText(getActivity(), "Upload succesful!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}

           /*
           dette er acitivityresult.

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBAOS = baos.toByteArray();

            cameraImageView.setImageBitmap(imageBitmap);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("gs://appproject-600c0.appspot.com");

            StorageReference imagesRef = storageReference.child("Photos");
            //Uri uri = data.getData();

            //StorageReference filepath = mStorageReference.child("Photos").child(uri.getLastPathSegment());

            UploadTask uploadTask = imagesRef.putBytes(dataBAOS);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    Picasso.get().load(downloadUri).into(cameraImageView);

                    Toast.makeText(getActivity(), "Upload succesfull!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Upload failed!", Toast.LENGTH_SHORT).show();
                }
            });

            */
