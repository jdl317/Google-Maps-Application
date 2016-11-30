package com.example.jefflitterst.googlemapapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
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
import java.util.ArrayList;


public class MapsActivity extends AppCompatActivity implements OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback/*, CameraBridgeViewBase.CvCameraViewListener2*/ {
    private GoogleMap mMap;
    private boolean mPermissionDenied = false;
    ArrayList<Place> places;
    ArrayList<Bitmap> photos = new ArrayList<Bitmap>();
    int howmanyplaces = 10;
    ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();

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
    final Context context = this;
    private Button button;
    PictureTaker pictureTaker;
    private Button pictures;
    private EditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SupportMapFragment mapFragment;
        super.onCreate(savedInstanceState);
        startPage();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void startPage()
    {
        //brings up start page
        setContentView(R.layout.start_screen);
        final Button button2 = (Button) findViewById(R.id.button2);

        //directions on click listener
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.instructions);
                final Button button4 = (Button) findViewById(R.id.button4);
                button4.setOnClickListener(new View.OnClickListener() {
                    //recursively goes back to start page
                    public void onClick(View v)
                    {
                        startPage();
                    }
                });
            }
        });

        //starting maps activity listener
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startMap();
            }
        });
    }

    public void startMap()
    {
        //starts the map fragment
        setContentView(R.layout.how_many_places);
        final Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                EditText text = (EditText) findViewById(R.id.editText2);
                howmanyplaces = Integer.parseInt(text.getText().toString());
                afterHowMany();
            }
        });
    }

    public void afterHowMany()
    {
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment  = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        try {
            // Set map fragment design scheme
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

        pictures = (Button)findViewById(R.id.button_camera);
        pictures.setOnClickListener(pictureListener);


        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.tvLocality);
                    TextView tvSnippet = (TextView) v.findViewById(R.id.tvSnippet);

                    LatLng latLng = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvSnippet.setText(marker.getSnippet());

                    ImageView iv = (ImageView) v.findViewById(R.id.imageView1);
                    if (photos != null) {
                        if (photos.size() == howmanyplaces) {
                            for (int i = 0; i < markers.size(); i++) {
                                if (marker.getPosition().equals(markers.get(i).getPosition())) {
                                    iv.setImageBitmap(photos.get(i));
                                }
                            }
                        }
                    }

                    return v;
                }
            });

            Button exercisebutton = (Button) (findViewById(R.id.button_distance));
            exercisebutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                    setContentView(R.layout.exercise_stats);
                    Button back = (Button) (findViewById(R.id.button6));
                    back.setOnClickListener(new View.OnClickListener() {
                       public void onClick(View view)
                       {

                       }
                    });
                }
            });

        }


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        try {

            Location myLocation = locationManager.getLastKnownLocation(provider);
            if(myLocation != null){
                final double latitude = myLocation.getLatitude();
                final double longitude = myLocation.getLongitude();

                //create a background thread to find nearby locations
                Background bg = new Background(latitude, longitude, "hospital|museum|library|book_store|art_gallery|bakery|casino|fire_station|gym|movie_theater|police|school|stadium|university|zoo", this);
                //execute thread
                bg.execute();
            }

        } catch (SecurityException se) {
            Log.d("NO Permissions", "Involving getting last known provider");
        }

    }

    public void function (ArrayList<Place> placeList)
    {
        System.out.println("DONE");
        //set result of background thread
        places = placeList;
        addMarkers();
    }

    public void addMarkers()
    {
        int k = 0;
        for (int i = 0; i < places.size() && k < howmanyplaces; i++) {

            ArrayList<Photo> photoref = places.get(i).getPhotos();
            if(!photoref.isEmpty()) {
                for(int j = 0; j < 1; j++)
                {
                    ImageBackground bg = new ImageBackground(photoref.get(j).getReference(), photoref.get(j).getHeight(), photoref.get(j).getWidth(), this);
                    bg.execute();
                }

                String clue = displayClue(places.get(i));
                MarkerOptions marker = new MarkerOptions()
                        .title("CLUE:")
                        .snippet(clue)
                        .position(new LatLng(places.get(i).getLatitude(), places.get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name));
                mMap.addMarker(marker);
                markers.add(marker);


                LatLng current = new LatLng(places.get(i).getLatitude(), places.get(i).getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                k++;
            }

        }

    }

    public String displayClue(Place place)
    {
        String clue = "";
        //hospital|museum|library|book_store|art_gallery|bakery|casino|fire_station|gym|movie_theater|police|school|stadium|university|zoo
        for(int l = 0; l < place.getTypes().length; l++)
        {
                if(place.getTypes()[l].equals("hospital"))
                {
                    clue = "You go here to get healed when you are sick";
                    break;
                }
                else if(place.getTypes()[l].equals("museum"))
                {
                    clue = "You definetly thought these were kind of boring on school field trips";
                    break;
                }
                else if(place.getTypes()[l].equals("library"))
                {
                    clue = "You go here to check out books";
                    break;
                }
                else if(place.getTypes()[l].equals("book_store"))
                {
                    clue = "You go here to buy books (or maybe you used to before Kindle!)";
                    break;
                }
                else if(place.getTypes()[l].equals("art_gallery"))
                {
                    clue = "You come here to look art";
                    break;
                }
                else if(place.getTypes()[l].equals("museum"))
                {
                    clue = "You come here to snack on sweets, yum!";
                    break;
                }
                else if(place.getTypes()[l].equals("casino"))
                {
                    clue = "You come here to gamble your $$";
                    break;
                }
                else if(place.getTypes()[l].equals("fire_station"))
                {
                    clue = "Here's where they keep the fire trucks";
                    break;
                }
                else if(place.getTypes()[l].equals("gym"))
                {
                    clue = "You come here to sweat!";
                    break;
                }
                else if(place.getTypes()[l].equals("movie_theater"))
                {
                    clue = "You come here to watch movies before they are on Netflix";
                    break;
                }
                else if(place.getTypes()[l].equals("police"))
                {
                    clue = "This is who picks up when you call 911";
                    break;
                }
                else if(place.getTypes()[l].equals("school"))
                {
                    clue = "You come here to learn";
                    break;
                }
                else if(place.getTypes()[l].equals("stadium"))
                {
                    clue = "You come here to cheer on your team (better not be Lafayette)";
                    break;
                }
                else if(place.getTypes()[l].equals("university"))
                {
                    clue = "Tough classes, a lot of studying, and a lot of fun can be found here";
                    break;
                }
                else if(place.getTypes()[l].equals("zoo"))
                {
                    clue = "This place has some pretty cool animals";
                    break;
                }
            }
        return clue;
    }

    public void getMap(Bitmap map)
    {
        photos.add(map);
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


    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Centering on Current Location", Toast.LENGTH_SHORT).show();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        try {
            Location myLocation = locationManager.getLastKnownLocation(provider);
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
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

    /*
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
    */

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

