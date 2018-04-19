package com.example.henrikfogbunzel.appproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

public class ProfileActivity extends FragmentActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new CameraFragment());
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

    private boolean loadMapFragment(SupportMapFragment supportMapFragment){
        if(supportMapFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, supportMapFragment)
                    .commit();
            return true;
        }
        return  false;
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
}
