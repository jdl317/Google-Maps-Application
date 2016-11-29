package com.example.jefflitterst.googlemapapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by emmawald on 11/26/16.
 */

public class Place {

    private String loggedIn;
    private String id;
    private String icon;
    private String name;
    private String vicinity;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private String reference;
    private ArrayList<Photo> photos;


    public ArrayList<Photo> getPhotos() {return photos;}

    public void setReference(ArrayList<Photo> photos) {this.photos = photos;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Place() {
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }



    static Place jsonToPontoReferencia(JSONObject pontoReferencia) {
        try {
            Place result = new Place();
            JSONObject geometry = (JSONObject) pontoReferencia.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(pontoReferencia.getString("icon"));
            result.setName(pontoReferencia.getString("name"));
            result.setVicinity(pontoReferencia.getString("vicinity"));
            result.setId(pontoReferencia.getString("id"));
            JSONArray jsonPhotos = pontoReferencia.optJSONArray("photos");
            ArrayList<Photo> photos = new ArrayList<>();
            if (jsonPhotos != null) {
                for (int i = 0; i < jsonPhotos.length(); i++) {
                    JSONObject jsonPhoto = jsonPhotos.getJSONObject(i);
                    String photoReference = jsonPhoto.getString("photo_reference");
                    int width = jsonPhoto.getInt("width"), height = jsonPhoto.getInt("height");
                    photos.add(new Photo(photoReference, width, height));
                }
            }
            result.photos = photos;
//            result.setRating((Double)pontoReferencia.get("rating"));
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

}
