package com.example.jefflitterst.googlemapapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends AppCompatActivity implements OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Hello"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this,
                    new String []{android.Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.GET_PERMISSIONS);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

        }
    }

    private void initializeLocations() {
        LatLng taylorGym = new LatLng(40.607479, -75.374098);
        LatLng zoellner = new LatLng(40.608167, -75.372677);
        LatLng universitycenter = new LatLng(40.606141, -75.378222);
        LatLng campussquare = new LatLng(40.609525, -75.378423);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(taylorGym));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(taylorGym));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));
        mMap.addMarker(new MarkerOptions().position(taylorGym).title("Welcome to Taylor Gym!").snippet("Skies out Thighs out!"));
        mMap.addMarker(new MarkerOptions().position(zoellner).title("Here's Zoellner!").snippet("So Artistic!"));
        mMap.addMarker(new MarkerOptions().position(universitycenter).title("The UC!").snippet("Coooool!"));
        mMap.addMarker(new MarkerOptions().position(campussquare).title("Campus Square!").snippet("Yippee!"));
        LatLng myCoordinates = taylorGym;//new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 20);
        mMap.animateCamera(yourLocation);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myCoordinates)      // Sets the center of the map to LatLng (refer to previous snippet)
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Polyline line1 = mMap.addPolyline(new PolylineOptions().add(taylorGym, zoellner).width(5).color(Color.RED));
        return;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "My Location button clicked", Toast.LENGTH_SHORT).show();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        try {
            Location myLocation = locationManager.getLastKnownLocation(provider);
            //double latitude = myLocation.getLatitude();
            //double longitude = myLocation.getLongitude();
            //LatLng latLng = new LatLng(latitude, longitude);
            initializeLocations();
        }catch(SecurityException se){
            Log.d("NO Permissions", "Involving getting last known provider");
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PackageManager.GET_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    enableMyLocation();

                } else {
                    mPermissionDenied = true;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }
}
