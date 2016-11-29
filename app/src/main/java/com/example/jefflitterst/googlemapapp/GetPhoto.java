package com.example.jefflitterst.googlemapapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by emmawald on 11/28/16.
 */

public class GetPhoto {

    private String API_KEY;

    public GetPhoto(String apikey) {
        this.API_KEY = apikey;
    }

    public Bitmap requestPhotos(String reference, int height, int width)
    {
        String urlString = makeUrl(reference, height, width);
        Bitmap map = getUrlContents(urlString);
        return map;
    }

    public String makeUrl(String reference, int height, int width)
    {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/photo?");
        urlString.append("photoreference=");
        urlString.append(reference);
        urlString.append("&maxheight=");
        urlString.append(Integer.toString(height));
        urlString.append("&maxwidth=");
        urlString.append(Integer.toString(width));
        urlString.append("&key=");
        urlString.append(API_KEY);
        Log.d("URL",urlString.toString());
        return urlString.toString();
    }

    private Bitmap getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream input = urlConnection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;

        }catch (Exception e) {
            e.printStackTrace();
        }
        //img = content
        return null;
    }

}
