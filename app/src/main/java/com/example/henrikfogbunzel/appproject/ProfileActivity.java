package com.example.henrikfogbunzel.appproject;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;



import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentTransaction;
//import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.henrikfogbunzel.appproject.services.LocationService;
import com.example.henrikfogbunzel.appproject.services.ProximityIntentReceiver;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

//fragment manager
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, GalleryFragment.OnMessageSendListener, MapFragment.OnMarkerDataPass  {
    /*
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds

    private static final long POINT_RADIUS = 1000; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1;

    private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
    */
    //List<MarkerOptions> markers = new ArrayList<MarkerOptions>();


    private LocationManager locationManager;
    private LocationService mLocationService = null;
    private boolean mBound = false;

    //handle orientation change
    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";
    private MapFragment mMapFragment;

    private Context mContext;

    private ProfileActivity mProfileActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = getApplicationContext();
        mProfileActivity = new ProfileActivity();

        Intent intent = new Intent(this, LocationService.class);

      //  if(){
            //startService(intent);
        //}
        //intent.putExtra("lat", lat);

        ServiceConnection con = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                LocationService.LocationServiceBinder bind = (LocationService.LocationServiceBinder) iBinder;
                mLocationService = bind.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBound = false;
                Log.d("BLA", "onServiceDisconnected: Service disconnected");
            }
        };

        bindService(intent, con, Context.BIND_AUTO_CREATE);



        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        //loadFragment(new CameraFragment());

        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }



/*
    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(mMapFragment).commit();
        }
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return true;
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null) {
            getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        //SupportMapFragment supportMapFragment = null;

        switch (item.getItemId()){
            case R.id.navigation_gallery:
                fragment = new GalleryFragment();
                break;
            case R.id.navigation_camera:
                fragment = new CameraFragment();
                break;
            case R.id.navigation_map:
                 fragment = new MapFragment();
                //supportMapFragment = new MapFragment();
                break;
        }
        return loadFragment(fragment);
       //return loadMapFragment(supportMapFragment);
    }

    @Override
    public void onMeassageSend(String latValue, String lngValue, Boolean flag) {

        MapFragment mapFragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putString("latValue", latValue);
        bundle.putString("lngValue", lngValue);
        bundle.putBoolean("flagValue", flag);
        mapFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapFragment, null);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
/*
    public void methodsForIntent(Double lat, Double lon, Float latf, Float lonf){
        onMarkerDataPass(lat, lon);
        floatReferenceDataPas(latf, lonf);
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);

        intent.putExtra("latf", latf);
        intent.putExtra("lonf", lonf);
    }
    */

    @Override
    public void onMarkerDataPass(Double lat, Double lon) {
        Log.d("onMarkerDataPass", "coordinates in doubles: " + lat + lon);

        if(lat != null && mBound) {
                mLocationService.addProximityAlert(lat, lon);
        }
        /*
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lat", lon);
        startService(intent);
        */
    }

    @Override
    public void floatReferenceDataPas(Float latf, Float lonf) {
        Log.d("onMarkerDataPass", "coordinates in floats: " + latf + lonf);

        if(latf != null && mBound) {
            mLocationService.saveCoordinatesInPreferences(latf, lonf);
        }
        /*
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("latf", latf);
        intent.putExtra("lonf", lonf);
        startService(intent);
        */
    }
}
