package com.example.jefflitterst.googlemapapp;

import java.io.InputStream;

/**
 * Created by emmawald on 11/29/16.
 */

public class Photo {

    /**
     * Represents a referenced photo.
     */
    private final String reference;
    private final int width, height;
    private InputStream image;

    protected Photo(String reference, int width, int height) {
        this.reference = reference;
        this.width = width;
        this.height = height;
    }


    /**
     * Returns the reference token to the photo.
     *
     * @return reference token
     */
    public String getReference() {
        return reference;
    }

    /**
     * Returns the width of the photo.
     *
     * @return photo width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the photo.
     *
     * @return photo height
     */
    public int getHeight() {
        return height;
    }


}
