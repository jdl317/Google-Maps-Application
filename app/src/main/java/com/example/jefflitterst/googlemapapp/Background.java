package com.example.jefflitterst.googlemapapp;

import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by emmawald on 11/26/16.
 */

public class Background extends AsyncTask<Void, Void, ArrayList<Place>> {

    private String API_KEY = "AIzaSyCZPjAC5CZCPS_09L1eR2qzXF6LFAfZX6A";
    public double lat;
    public double lng;
    public String plc;
    public ArrayList<Place> list;
    private MapsActivity p;

    public Background(double lat, double lng, String plc, MapsActivity p)
    {
        this.lat = lat;
        this.lng = lng;
        this.plc = plc;
        this.p = p;
    }

    @Override
    protected ArrayList<Place> doInBackground(Void... voids) {
        System.out.println("IN BACKGROUND");
        GetPlace placeSearch = new GetPlace(API_KEY);
        ArrayList<Place> places = placeSearch.findPlaces(this.lat, this.lng, plc);
        return places;
    }

    @Override
    protected void onPostExecute(ArrayList<Place> result) {
        p.function(result);
    }
}
