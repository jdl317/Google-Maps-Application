package com.example.jefflitterst.googlemapapp;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by emmawald on 11/28/16.
 */

public class ImageBackground extends AsyncTask<Void, Void, Bitmap> {

    private String API_KEY = "AIzaSyCZPjAC5CZCPS_09L1eR2qzXF6LFAfZX6A";
    public String reference;
    public int width;
    public int height;
    MapsActivity p;

    public ImageBackground(String reference, int height, int width, MapsActivity p)
    {
        this.reference = reference;
        this.height = height;
        this.width = width;
        this.p = p;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        GetPhoto getter = new GetPhoto(API_KEY);
        Bitmap map = getter.requestPhotos(reference, height, width);
        return map;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        p.getMap(result);
    }
}
