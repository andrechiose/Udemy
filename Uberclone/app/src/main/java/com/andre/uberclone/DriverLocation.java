package com.andre.uberclone;

import android.content.Intent;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DriverLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        LatLng driverLocation = new LatLng(intent.getDoubleExtra("driverLatitude", 0),
                intent.getDoubleExtra("driverLongitude",0));
        LatLng requestLocation = new LatLng(intent.getDoubleExtra("requestLatitude",0),
                intent.getDoubleExtra("requestLongitude",0));

        Marker markerDriver = mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your location"));
        Marker markerRequest = mMap.addMarker(new MarkerOptions().position(requestLocation).title("Rider location"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(markerDriver.getPosition());
        builder.include(markerRequest.getPosition());

        int padding = 30;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);

        mMap.animateCamera(cameraUpdate);
    }
}
