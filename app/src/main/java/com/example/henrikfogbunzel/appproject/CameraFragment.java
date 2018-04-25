package com.example.henrikfogbunzel.appproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.henrikfogbunzel.appproject.model.ImagesModel;
import com.example.henrikfogbunzel.appproject.model.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment {

    Button cameraBtnCapture;
    ImageView cameraImageView;
    ProgressBar progressBar;

    private Uri mImageUri;

    //private Uri imageUriString;
    //String imageUriString;

    private ImagesModel mImagesModel;

    //private StorageTask mStorageTask;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    StorageReference mStorageReference;
    FirebaseAuth auth;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String TAG = "CameraFragment";

    //for Location manager and current location.
    private static final int REQUEST_LOCATION = 2;
    LocationManager locationManager;
    String lattitude, longitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        cameraBtnCapture = (Button) view.findViewById(R.id.cameraBtnCapture);
        cameraImageView = (ImageView) view.findViewById(R.id.cameraImageView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        //For location
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        auth = FirebaseAuth.getInstance();

        mStorageReference = FirebaseStorage.getInstance().getReference();

        cameraBtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LocationManager getCurrentPosition
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    buildAlertMessageNoGps();
                } else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    getLocation();
                }

                //camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        });

        return view;
    }

    private void getLocation() {
        //check permission in a fragment: https://stackoverflow.com/questions/40760625/how-to-check-permission-in-fragment
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Log.d(TAG, "location: lat = " + lattitude + " long = " + longitude);
                //textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                  //      + "\n" + "Longitude = " + longitude);
            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Log.d(TAG, "location1: lat = " + lattitude + " long = " + longitude);
                //textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude + "\n" + "Longitude = " + longitude);
            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Log.d(TAG, "location2: lat = " + lattitude + " long = " + longitude);
                //textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude + "\n" + "Longitude = " + longitude);
            }else{
                Toast.makeText(getActivity(),"Unable to trace your location",Toast.LENGTH_SHORT).show();
            }
        }

    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

            UUID imgUUID = UUID.randomUUID();
            final String imgUIIDString = imgUUID.toString();

            //StorageReference storageReference = mStorageReference.child("users/" + userID + "/" + UUID.randomUUID() );
            //StorageReference storageReference = mStorageReference.child("users/" + userID + "/" + "/"+imgUUID+"/" + imgUUID );

            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference("users/" + userID + "/"+imgUIIDString+"/");

            StorageReference storageReference = mStorageReference.child("users/" + userID + "/"+imgUIIDString+"/" + imgUUID);
/*
            Map<String, String> map = new HashMap<>();
            map.put("URI",  imageUriString);
            map.put("UUID", imgUIIDString);
            map.put("Lat", lattitude);
            map.put("Lon", longitude);
            mDatabaseReference.setValue(map);
*/
            UploadTask uploadTask = storageReference.putBytes(dataByte);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);

                    mImageUri = taskSnapshot.getDownloadUrl();

                    //Upload upload = new Upload(taskSnapshot.getDownloadUrl().toString());

                    //String imageUriString = taskSnapshot.getDownloadUrl().toString();
                    writeNewImages(mImageUri.toString(), imgUIIDString, lattitude, longitude);

                    Toast.makeText(getActivity(), "Upload succesful!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Upload failed!", Toast.LENGTH_SHORT).show();
                }
            });



            //String imageUriString = "";
            //writeNewImages(imageUriString, imgUIIDString, lattitude, longitude);


            //https://androidstudy.com/2017/04/17/firebase-realtime-database/
            //ImagesModel imagesModel = new ImagesModel(imgUIIDString, imgUIIDString, lattitude, longitude);

           // String imageUriString = mImageUri.toString();

            /*
            Map<String, String> map = new HashMap<>();
            //map.put("URI",  imageUriString);
            map.put("UUID", imgUIIDString);
            map.put("Lat", lattitude);
            map.put("Lon", longitude);
            mDatabaseReference.setValue(map);
            */
        }
    }

    private void writeNewImages(String imageUriString, String imgUIIDString, String lattitude, String longitude){
        ImagesModel imagesModel = new ImagesModel(imageUriString, imgUIIDString, lattitude, longitude);

        mDatabaseReference.setValue(imagesModel);
       // Map<String, Object> imageModelValues = imagesModel.toMap();

    }
}





















/*

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


            UUID imgUUID = UUID.randomUUID();
            String imgUIIDString = imgUUID.toString();
            String imageUriString = mImageUri.toString();
            //StorageReference storageReference = mStorageReference.child("users/" + userID + "/" + UUID.randomUUID() );
            StorageReference storageReference = mStorageReference.child("users/" + userID + "/" + "/"+imgUUID+"/" + imgUUID );


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



 */


