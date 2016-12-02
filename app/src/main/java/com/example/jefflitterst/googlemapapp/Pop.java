package com.example.jefflitterst.googlemapapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by emmawald on 11/30/16.
 */
public class Pop extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle b = getIntent().getExtras();
        double distance = -1;
        if (b != null)
        {
            distance = b.getDouble("key");
        }

        double distanceMiles = distance *0.000621371192;

        //assuming 90 calories per mile for the moment
        double calories = distanceMiles * 90;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_window);
        TextView dist = (TextView) findViewById(R.id.dist);
        TextView cals = (TextView) findViewById(R.id.cals);

        dist.setText(distanceMiles + "");
        cals.setText(calories + "");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

/*        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));*/

    }
}
