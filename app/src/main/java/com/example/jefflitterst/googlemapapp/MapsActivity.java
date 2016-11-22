package com.example.jefflitterst.googlemapapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MapsActivity extends AppCompatActivity implements OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback/*, CameraBridgeViewBase.CvCameraViewListener2*/ {
    public final static String EXTRA_MESSAGE = "com.example.jefflitterst.googlemapapp.MESSAGE";
    private GoogleMap mMap;
    private boolean mPermissionDenied = false;


    private static final String TAG = "MapsActivity";
    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    final Context context = this;
    private Button button;
    PictureTaker pictureTaker;
    private Button pictures;
    private EditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        button = (Button) findViewById(R.id.buttonPrompt);
        //result = (EditText) findViewById(R.id.editTextResult);
        final String[] result = new String[1];

        pictures = (Button)findViewById(R.id.button_camera);
        pictures.setOnClickListener(pictureListener);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.map_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        result[0] = String.valueOf(userInput.getText());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }
    private View.OnClickListener pictureListener = new View.OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(MapsActivity.this, PictureTaker.class));
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
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

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        try {

            Location myLocation = locationManager.getLastKnownLocation(provider);
            if(myLocation != null){
                double latitude = myLocation.getLatitude();
                double longitude = myLocation.getLongitude();
                updateDistance(myLocation);
                //LatLng latLng = new LatLng(latitude, longitude);
                initializeLocations();
            }

        } catch (SecurityException se) {
            Log.d("NO Permissions", "Involving getting last known provider");
        }

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.GET_PERMISSIONS);
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

        String places = doInBackground(placesSearchStr);
        System.out.println(places);
        //displayPlacePicker();   
        // Polyline line1 = mMap.addPolyline(new PolylineOptions().add(taylorGym, zoellner).width(5).color(Color.RED)); 
    }

    String API_KEY = "AIzaSyBPIAXXBgE5YCelxh30EfcsBzkxkQBtnBc";
    double lat = 40.607479;
    double lng = -75.374098;
    String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
            "json?location="+lat+","+lng+
            "&radius=1000&sensor=true" +
            "&types=hospital|health"+
            "&key=" + API_KEY;

    String uri = "https://maps.googleapis.com/maps/api/place/search/json?location=50.936364,5.873566&radius=500&name=ziekenhuis&sensor=false&key=AIzaSyBPIAXXBgE5YCelxh30EfcsBzkxkQBtnBc";
    protected String doInBackground(String placesURL) {
        //fetch places 
        StringBuilder placesBuilder = new StringBuilder();
        //for (String placeSearchURL : placesURL) {
            try {

                URL requestUrl = new URL(placesURL);
                HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = null;
                    InputStream inputStream = connection.getInputStream();
                    if (inputStream == null) {
                        return "";
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        placesBuilder.append(line + "\n");
                    }
                    if (placesBuilder.length() == 0) {
                        return "";
                    }
                    Log.d("test", placesBuilder.toString());
                }
                else {
                    Log.i("test", "Unsuccessful HTTP Response Code: " + responseCode);
                }
            } catch (MalformedURLException e) {
                Log.e("test", "Error processing Places API URL", e);
            } catch (IOException e) {
                Log.e("test", "Error connecting to Places API", e);
            }
        //}
        return placesBuilder.toString();
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Centering on Current Location", Toast.LENGTH_SHORT).show();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        try {
            Location myLocation = locationManager.getLastKnownLocation(provider);
            //double latitude = myLocation.getLatitude();
            //double longitude = myLocation.getLongitude();
            //LatLng latLng = new LatLng(latitude, longitude);
            initializeLocations();
        } catch (SecurityException se) {
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

    public void updateDistance(Location startLocation)
    {
        GPSTracker mGPS = new GPSTracker(this);
        if(mGPS.canGetLocation ){
            Location current = mGPS.getLocation();
            double dist = current.distanceTo(startLocation);
            TextView t = (TextView)findViewById(R.id.text_view);
            String setText = "distance: " + dist;
            t.setText(setText);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
