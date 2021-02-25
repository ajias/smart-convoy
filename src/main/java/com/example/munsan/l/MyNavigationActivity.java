package com.example.munsan.l;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;


public class MyNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener {

    FirebaseAuth auth;
    DatabaseReference databaseReference, usingCode, notification,event;
    String currentUserID, code;

    String userName, userCode;
    TextView t1name, t2code;

    private GoogleMap mMap;
    GoogleApiClient client;
    Location mLastLocation;
    Marker currentLocationMarker;
    LocationRequest locationRequest;
    List<Address> addressList = null;
    private Address myAddress;
    LatLng latLng,latLngg;
    private static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude, longitude;
    String duration = "";
    String distance = "";
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton fab1, fab2, fab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.fabMenu);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //requestPermission();

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        t1name = header.findViewById(R.id.title_text);
        t2code = header.findViewById(R.id.title_code);

        //client = LocationServices.getFusedLocationProviderClient(this);
        //getLocationForFirebase();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue(String.class);
                userCode = dataSnapshot.child("code").getValue(String.class);

                t1name.setText(userName);
                t2code.setText("Your circle code : "+ userCode);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setupUsingCode();





        fab1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("circleMembers")){
                            code = dataSnapshot.child("code").getValue(String.class);
                            setNotification();

                            notification.setValue("1");
                        }
                        else if(!dataSnapshot.child("joincode").getValue(String.class).equals("na")) {
                            code = dataSnapshot.child("joincode").getValue(String.class);
                            setNotification();

                            notification.setValue("1");
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Please join circle or let other join your circle before using this alert button !", Toast.LENGTH_LONG).show();
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setDefaultValue();
                            }
                        }, 5000);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vi) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("circleMembers")){
                            code = dataSnapshot.child("code").getValue(String.class);
                            setNotification();

                            notification.setValue("2");

                        }
                        else if(!dataSnapshot.child("joincode").getValue(String.class).equals("na")) {
                            code = dataSnapshot.child("joincode").getValue(String.class);
                            setNotification();

                            notification.setValue("2");
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Please join circle or let other join your circle before using this alert button !", Toast.LENGTH_LONG).show();
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setDefaultValue();
                            }
                        }, 5000);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vie) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("circleMembers")){
                            code = dataSnapshot.child("code").getValue(String.class);
                            setNotification();

                            notification.setValue("3");

                        }
                        else if(!dataSnapshot.child("joincode").getValue(String.class).equals("na")) {
                            code = dataSnapshot.child("joincode").getValue(String.class);
                            setNotification();

                            notification.setValue("3");

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Please join circle or let other join your circle before using this alert button !", Toast.LENGTH_LONG).show();
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setDefaultValue();
                            }
                        }, 5000);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        getNotification();
    }

    private void setDefaultValue() {
        notification.setValue(0);
    }

    private void setupUsingCode() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("circleMembers")){
                    code = dataSnapshot.child("code").getValue(String.class);

                    usingCode = databaseReference.child("usingCode");
                    usingCode.setValue(code);

                }
                else if(!dataSnapshot.child("joincode").getValue(String.class).equals("na")) {
                    code = dataSnapshot.child("joincode").getValue(String.class);

                    usingCode = databaseReference.child("usingCode");
                    usingCode.setValue(code);
                }
                else{
                    //do nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setNotification() {
        notification = FirebaseDatabase.getInstance().getReference().child("Event").child(code).child("notification");
    }

    private void getNotification() {
        databaseReference.child("usingCode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.getValue(String.class).equals("na")){
                    String iniCode = dataSnapshot.getValue(String.class);
                    event = FirebaseDatabase.getInstance().getReference().child("Event");

                    event.child(iniCode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("notification")){

                                if(dataSnapshot.child("notification").getValue().equals("1")){
                                    Toast.makeText(getApplicationContext(), "Hello, someone wanna go to Gas Station, please respond in chatRoom", Toast.LENGTH_LONG).show();

                                }else if(dataSnapshot.child("notification").getValue().equals("2")){
                                    Toast.makeText(getApplicationContext(), "Hello, someone wanna go to toilet, please respond in chatRoom", Toast.LENGTH_LONG).show();


                                }else if(dataSnapshot.child("notification").getValue().equals("3")){
                                    Toast.makeText(getApplicationContext(), "Hello, someone in emergency, please respond in chatRoom", Toast.LENGTH_LONG).show();

                                }else{
                                    //do nothing
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_joinCircle) {

            Intent myintent = new Intent(MyNavigationActivity.this, JoinCircleActivity.class);
            startActivity(myintent);

        } else if(id == R.id.nav_leaveCircle){
            Intent leavecircle = new Intent(MyNavigationActivity.this, LeaveCircleNew.class);
            startActivity(leavecircle);
        }
        else if (id == R.id.nav_chat) {

            Intent chat = new Intent(MyNavigationActivity.this, ChatActivity.class);
            startActivity(chat);

        } else if (id == R.id.nav_signout) {

            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                auth.signOut();
                finish();
                Intent intent = new Intent(MyNavigationActivity.this, IndexActivity.class);
                startActivity(intent);

            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (client == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }return;
        }

    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {


            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    public void onClick(View v)
    {
        Object dataTransfer[] = new Object[2];
        switch (v.getId()){
            case R.id.B_search:
                EditText tf_location = (EditText)findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                MarkerOptions mo = new MarkerOptions();
                List<Address> addressList = null;
                Geocoder geocoder = new Geocoder(this);

                try {
                    addressList = geocoder.getFromLocationName(location, 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addressList.size() > 0){
                    myAddress = addressList.get(0);
                    LatLng latLngg = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                    mo.position(latLngg)
                            .title(myAddress.getAddressLine(0))
                            .snippet("Distance = "+distance+" Duration = "+duration);
                    mMap.addMarker(mo);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngg));

                }
                String url = getDirectionsUrl();
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

                break;

        }
    }

    private String getDirectionsUrl()
    {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+latitude+","+longitude);
        googleDirectionsUrl.append("&destination="+myAddress.getLatitude()+","+myAddress.getLongitude());
        googleDirectionsUrl.append("&key="+"AIzaSyCJ9cDqk1r1RXrG6R2mLGmQe510JShBPQE");

        return googleDirectionsUrl.toString();
    }




    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);

        }
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);

            }
            return false;
        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        if (currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        latLng = new LatLng(location.getLatitude(),location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if (client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
        return;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        marker.setDraggable(true);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            MyNavigationActivity.TaskParser taskParser = new MyNavigationActivity.TaskParser();
            taskParser.execute(s);

        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DataParser directionsParser = new DataParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {

            ArrayList <LatLng> points = null;

            PolylineOptions polylineOptions = null;

            for (int i=0; i<lists.size(); i++) {
                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(0);

                for (int j=0; j<path.size();j++){
                    HashMap<String, String> point = path.get(j);

                    if (j==0){
                        distance = (String)point.get("Distance");
                        continue;
                    }else if (j == 1){
                        duration =(String)point.get("Duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points)
                        .width(15)
                        .color(Color.BLUE)
                        .geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();            }

        }
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }




}