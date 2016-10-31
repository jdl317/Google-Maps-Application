package com.example.jefflitterst.googlemapapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

public class DisplayMessageActivity extends AppCompatActivity {

    EditText mEdit;
    String distanceToGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MapsActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);
    }

    public void enterDist(View view) {
        mEdit   = (EditText)findViewById(R.id.edit_message);
        distanceToGo = mEdit.getText().toString();
        mapManager();

        //ViewGroup layout = (ViewGroup)findViewById(R.id.map_layout);
    }

    public void mapManager () {
        setContentView(R.layout.map_layout);
    }
}
