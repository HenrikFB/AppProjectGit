package com.example.henrikfogbunzel.appproject;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.henrikfogbunzel.appproject.adaptors.CustomInfoWindowAdapter;
import com.example.henrikfogbunzel.appproject.googleSamples.PlaceAutocompleteAdapter;
import com.example.henrikfogbunzel.appproject.model.ImagesModel;
import com.example.henrikfogbunzel.appproject.model.MyItem;
import com.example.henrikfogbunzel.appproject.model.PlaceInfo;
import com.example.henrikfogbunzel.appproject.services.GeofenceTransitionsIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResults;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MapFragment";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private ClusterManager<MyItem> mClusterManager;//skal slette

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    Marker mMarker;
    private final List<Marker> mMarkerList = new ArrayList<Marker>();

    //firebase
    private FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    String imgUIIDString;

    String latValue;
    String lngValue;
    // Bundle bundle;
    Boolean flag;

    //geofencing
    private GoogleApiClient googleApiClient;
    protected static final String TAGGEO = GeofenceTransitionsIntentService.class.getSimpleName();
    private static final float GEOFENCE_RADIUS = 1606; // 50 meters
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours
    private Location lastLocation;
    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    //step 8.
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    //private static final float GEOFENCE_RADIUS = 500.0f; // in meters

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private Circle geoFenceLimits;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_map, null);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        isServicesOK();
        getLocationPermission();

        if (savedInstanceState == null) {
            //bundle = this.getArguments();
            Bundle bundle = this.getArguments();

            try {
                latValue = bundle.getString("latValue");
                lngValue = bundle.getString("lngValue");
                flag = bundle.getBoolean("flagValue");
                Toast.makeText(getContext(), "Received: " + latValue + " " + lngValue + " " + flag.booleanValue(), Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                System.out.print("Caught the NullPointerException");
                flag = false;
            }
        } else {
            flag = false;
        }

        createGoogleApi();


        return view;
    }

    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if( mMarker != null ) {
            Geofence geofence = createGeofence( mMarker.getPosition(), GEOFENCE_RADIUS );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence( geofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

    //BLIVER IKKE KAlDT
    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;
        Intent intent = new Intent(getContext(), GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(
                getContext(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");

        //getActivity().getApplicationContext()
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                createGeofencePendingIntent()
        );//.setResultCallback(this);
    }

        // Bliver ikke kaldt
        // Create a Geofence
    private Geofence createGeofence(LatLng latLng, float radius){
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    //bliver ikke kaldt
    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //https://stackoverflow.com/questions/33706261/googleapiclient-is-not-connected-yet-even-though-onconnected-is-called-and-im
        //googleApiClient.connect();
        if(!googleApiClient.isConnected()){
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(getActivity(), "Map is Ready", Toast.LENGTH_SHORT).show();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();

        //mDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + userID + "/" + "/"+imgUIIDString+"/");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("users/" + userID + "/");

        //https://viblo.asia/p/firebase-realtime-database-in-android-WwXKPXlwKEJ

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/" + userID);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ImagesModel imagesModel = dataSnapshot.getValue(ImagesModel.class);


                Double lat = Double.parseDouble(imagesModel.getLattitude());
                Double lon = Double.parseDouble(imagesModel.getLongitude());

                //https://stackoverflow.com/questions/43127222/set-text-to-textview-if-an-image-has-been-selected-from-gallery
                //ikke prøvet.
/*
                View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_info_window, null);
                ImageView img = (ImageView) v.findViewById(R.id.img);
                String uriString = imagesModel.getImageUriString();
                Uri fileUri = Uri.parse(uriString);
                img.setImageURI(fileUri);
*/
                LatLng newLocation = new LatLng(lat, lon);
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(newLocation)
                        //.title(dataSnapshot.getKey()));
                        .title(imagesModel.getImageUriString()));
                startGeofence();

                //.icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                //prøv evt: https://developer.android.com/reference/android/widget/ImageView#setImageIcon(android.graphics.drawable.Icon)
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));

        if (mLocationPermissionsGranted) {
            if (flag) {
                moveCamerFromGallery();
            } else {
                getDeviceLocation();
            }
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            //My search bar block it anyway and make it later as custom icon.
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void moveCamerFromGallery() {
        Log.d(TAG, "moveCamerFromGallery: had been called");
        Double lat = Double.parseDouble(latValue);
        Double lon = Double.parseDouble(lngValue);
        moveCamera(new LatLng(lat, lon), DEFAULT_ZOOM);
        flag = false;
        //LatLng latLngBundle = new LatLng(lat, lon);
        //moveCamera(latLngBundle,DEFAULT_ZOOM);
//            moveCamera(new LatLng(lat, lon), DEFAULT_ZOOM);
            /*
            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
             */
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        //mMap.clear();
    }

    private boolean isServicesOK() {
        Log.d(TAG, "isServiceOK: checking google services version");

        int avaible = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (avaible == ConnectionResult.SUCCESS) {
            //Everything is fine and user can make map request
            Log.d(TAG, "isServiceOK: Google play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(avaible)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), avaible, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true; //later stuff
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    //this method was before implmenting geofencing.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // GoogleApiClient.ConnectionCallbacks connected
        Log.i(TAG, "onConnected()");
        getLastKnownLocation();

    }

    //getActivity().getApplicationContext() ligesom den ovenover.

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            Log.i(TAG, "LasKnown location. " +
                    "Long: " + lastLocation.getLongitude() +
                    " | Lat: " + lastLocation.getLatitude());
            writeLastLocation();
            startLocationUpdates();
        } else {
            Log.w(TAG, "No location retrieved yet");
            startLocationUpdates();
        }

    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private void startLocationUpdates() {

        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    //this method was before implmenting geofencing.
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }
    //this method was before implmenting geofencing.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //denne bliver kaldt
        Log.w(TAG, "onConnectionFailed()");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);

    }

    // Write location coordinates on UI. probably gonna delete it.
    private void writeActualLocation(Location location) {
        Log.d( TAG, "writeActualLocation: Lat: " + location.getLatitude() );
        Log.d( TAG,"writeActualLocation: Long: " + location.getLongitude() );
    }

    //bliver ikke kaldt
    public void onResult(@NonNull Status status) {

        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            drawGeofence();
        } else {
            // inform about fail
        }
    }

    //bliver ikke kaldt
    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( mMarker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( GEOFENCE_RADIUS );
        geoFenceLimits = mMap.addCircle( circleOptions );
    }
}

