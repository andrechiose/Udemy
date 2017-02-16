package com.andre.uberclone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestActivity extends Activity {

    private ListView requestListView;
    private ArrayList<String> requests;
    private ArrayList<LatLng> requestLatLng;
    private ArrayAdapter adapter;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        setTitle("Nearby Requests");
        requestListView = (ListView) findViewById(R.id.request_listview);

        requestLatLng = new ArrayList<>();
        requests = new ArrayList<>();
        requests.add("Getting nearby locations");
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);
        requestListView.setAdapter(adapter);

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(requestLatLng.size() > i){
                    try{
                        Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        Intent intent = new Intent(getApplicationContext(), DriverLocation.class);
                        intent.putExtra("requestLatitude", requestLatLng.get(i).latitude);
                        intent.putExtra("requestLongitude", requestLatLng.get(i).longitude);
                        intent.putExtra("driverLatitude", lastKnowLocation.getLatitude());
                        intent.putExtra("driverLongitude", lastKnowLocation.getLongitude());

                        startActivity(intent);
                    } catch(SecurityException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        setUpLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpLocation();
            }
        }
    }

    private void setUpLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        updateListView(location);
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);

                Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateListView(lastKnowLocation);
            }
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateListView(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateListView(lastKnowLocation);
        }

    }

    private void updateListView(Location location) {
        if (location == null) return;

        requests.clear();
        requestLatLng.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");

        final ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        query.whereNear("location", geoPoint);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {
                            ParseGeoPoint requestLocation = object.getParseGeoPoint("location");

                            if(requestLocation != null){
                                Double distanceInKM = geoPoint.distanceInKilometersTo(requestLocation);

                                Double distanceOneDP = (double) Math.round(distanceInKM * 10) / 10;

                                requestLatLng.add(new LatLng(requestLocation.getLatitude(), requestLocation.getLongitude()));
                                requests.add(String.valueOf(distanceOneDP) + "km");
                            }
                        }

                    } else {
                        requests.add("No active requests nearby");
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);
        requestListView.setAdapter(adapter);
    }
}
