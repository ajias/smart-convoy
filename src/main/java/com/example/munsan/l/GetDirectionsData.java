package com.example.munsan.l;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @auth Priyanka
 */

public class GetDirectionsData extends AsyncTask<String, Void, String> {

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    LatLng latLng;


    @Override
    protected String doInBackground(String... strings) {
        return null;
    }
}

