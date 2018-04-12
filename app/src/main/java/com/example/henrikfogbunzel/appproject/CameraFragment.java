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
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.henrikfogbunzel.appproject.model.ImagesModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment {

    Button cameraBtnCapture;
    ImageView cameraImageView;
    ProgressBar progressBar;

    private Uri mImageUri;

    private ImagesModel mImagesModel;

    //private StorageTask mStorageTask;

    private StorageReference mStorageReference;
    private FirebaseAuth auth;

    private static final int CAMERA_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        cameraBtnCapture = (Button) view.findViewById(R.id.cameraBtnCapture);
        cameraImageView = (ImageView) view.findViewById(R.id.cameraImageView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        /*
        Skal jeg lave:
        mStorageReference = FirebaseStorage.getInstance().getReference(""images/users/" + userID");
         */
        mStorageReference = FirebaseStorage.getInstance().getReference();

        cameraBtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK ){

            progressBar.setVisibility(View.VISIBLE);

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


            //skal jeg ik bruge push?
            //UUID.randomUUID()
            StorageReference storageReference = mStorageReference.child("users/" + userID + "/" + UUID.randomUUID());

            UploadTask uploadTask = storageReference.putBytes(dataByte);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Upload failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    //Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mImageUri = taskSnapshot.getDownloadUrl();
                    //mImagesModel.setImageUrl(mImageUri);
                    Toast.makeText(getActivity(), "Upload succesful!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}


