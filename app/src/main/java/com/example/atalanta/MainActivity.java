package com.example.atalanta;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import static java.lang.Math.cos;


public class MainActivity extends FragmentActivity implements  OnMapReadyCallback{
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private RequestQueue requestQueue;
    private String url = "https://www.mapquestapi.com/directions/v2/alternateroutes";
    private static final String TAG = "ApiRequest";
    private final int[] colors = {Color.BLUE,Color.RED,Color.DKGRAY};
    private LatLng mLastKnownLatLng = new LatLng(37.4220, -122.0841); // subject to update
    private UiSettings mUiSettings;

    // VARIABLES FOR TESTING
    private final LatLng googlePlex = new LatLng(37.4220, -122.0841); // google plex
    private final LatLng applePark  = new LatLng(37.3349, -122.0091); // apple park
    private final LatLng facebook  = new LatLng(37.4851, -122.1483); // facebook hq
    Button test_button;

    //Variables for navigation bar
    private Button health, profile;
    private String[] mileOptions;
    private static String selectedMileage = "30";
    public Boolean moveCam = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(this);

        // Navigation bar with buttons and scroller on bottom of screen
        //Get reference of button
        health = (Button) findViewById(R.id.navHealth);
        //Perform setOnClickListener on first button
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_health = new Intent(getApplicationContext(), HealthActivity.class);
                startActivity(intent_health);
            }
        });

        profile = (Button) findViewById(R.id.navProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        mileOptions = new String[6];
        mileOptions[0] = "Dest ";
        for(int i = 3; i <= 15; i=i+3){
            mileOptions[i/3] = "   ".concat(String.valueOf(i)).concat("   ") ;
        }
        Spinner miles = (Spinner) findViewById(R.id.milesNum);
        miles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMileage = parent.getItemAtPosition(position).toString();
                //Check that mileage variable is updated
                Log.d("MILEAGE: ", selectedMileage);

                // only generate route if not chosen dest in miles box
                if(!selectedMileage.trim().toLowerCase().equals("dest"))
                {
                    mMap.clear();
                    moveCam = false;
                    Random rand = new Random();
                    // CAN CHECK ROUTE DISTANCE IN LOG "distance list" from asyn worker
                    randomRouteGenerator(Double.valueOf(selectedMileage),rand);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("MILEAGES: ", "spinner disappeared");
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, mileOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        miles.setAdapter(adapter);

        //Test button for accessing login page, navbar onclick implemented
        test_button = (Button) findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(applePark));
//                sendAndRequestResponse(applePark,"1");
                displayMyLocation();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // map settings
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setAllGesturesEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);
        displayMyLocation(); // called in getMapAsync since want map ready then display current marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                // Get those dank routes only if spinner is selected at "Dest"
                if (selectedMileage.trim().toLowerCase().equals("dest"))
                {
                    // start clean
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(mLastKnownLatLng).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    // moveCam true so the asyn worker can set the map camera
                    moveCam = true;
                    // Add new marker to the Google Map Android API V2
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    sendAndRequestResponse(latLng, "3");
                }
                else {
                    Toast.makeText(getApplicationContext(),"To set destination? Change MODE",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        {
            // if request is cancelled, result arrays are empty
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                displayMyLocation();
        }
    }

    /**
     *
     * @param distance distance desired acquired from MODE spinner
     * @param rand  A random number generator, no need to waste to generate another every call
     */
    private void randomRouteGenerator(Double distance, Random rand) {
        // bounding box mechanism, approx circular reach as box, lat doesn't change over 10 mile so can use cos of center lat
        Double df = distance/2/69;  // North-south distance in degrees
        Double dl = df / cos(mLastKnownLatLng.latitude); // East-west distance in degrees
        Double sLat = mLastKnownLatLng.latitude - df;
        Double nLat = mLastKnownLatLng.latitude + df;
        Double wLng = mLastKnownLatLng.longitude - dl;
        Double eLng = mLastKnownLatLng.longitude + dl;

        LatLng destination = mLastKnownLatLng;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mLastKnownLatLng).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


        // randomly generate 3 different destinations on box, change for more routes
        for(int i = 0; i<3; i++)
        {
            /*
             side = 0: west side of box, lng use wLng
             side = 1: south side of box, lat use sLat
             side = 2: east side of box, lng use eLng
             side = 3: north side of box, lat use nLat
            */
            int side = rand.nextInt(4);
            switch (side){
                case 0:
                    destination = new LatLng(2*df*rand.nextDouble()+sLat,wLng);
                    break;
                case 1:
                    destination = new LatLng(sLat, 2*dl*rand.nextDouble()+wLng);
                    break;
                case 2:
                    destination = new LatLng(2*df*rand.nextDouble()+sLat,eLng);
                    break;
                case 3:
                    destination = new LatLng(nLat, 2*dl*rand.nextDouble()+wLng);
                    break;
            }
            mMap.addMarker(new MarkerOptions().position(destination).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            sendAndRequestResponse(destination,"1");
        }
        // move camera to fit all generated routes
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                new LatLngBounds(new LatLng(sLat,wLng),new LatLng(nLat,eLng)), 280));// southwest and northeast to determine bounds, 200 padding

    }


    /**
     * Function to place HUE_AZURE marker at current location, moves camera
     */
    private void displayMyLocation() {
        // Check if permission granted
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        // if not, ask for permission
        if(permission == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // if granted, display marker at current location
        else
        {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this,task ->
            {
                Location mLastKnownLocation = task.getResult();
                if(task.isSuccessful() && mLastKnownLocation != null)
                {
                    mLastKnownLatLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                    // move camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mLastKnownLatLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                    // add current position marker
                    mMap.addMarker(new MarkerOptions().position(mLastKnownLatLng).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            });
        }
    }

    /**
     * Top level function to get route from calling MapQuest API, checks for current location first
     * @param dest destination of route
     * @param max max routes to generate for given destination
     */
    private void sendAndRequestResponse(LatLng dest, String max) {
        // Check if permission granted
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        // if not, ask for permission
        if(permission == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // if granted
        else
        {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this,task ->
            {
                Location mLastKnownLocation = task.getResult();
                if(task.isSuccessful() && mLastKnownLocation != null)
                {
                    LatLng mLastKnownLatLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                    // Action
                    // RequestQueue initialized
                    requestQueue = Volley.newRequestQueue(this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, getDirectionsUrl(mLastKnownLatLng,dest, max), null, new Response.Listener<JSONObject>() {
                                // use googleplex as backup origin
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Do the heavy duty of parsing json on async task
                                    new parseDrawWorker().execute(response);
                                }
                            }, error -> Log.d(TAG,"Error: "+ error.toString()));
                    requestQueue.add(jsonObjectRequest);
                }
            });
        }

    }


    /**
     *Helper function for constructing the API query
     * @param origin origin of route, always current location in current setting
     * @param dest  destination of route
     * @param max max routes to generate with origin, destination pair, passed from sendAndRequestResponse()
     * @return html query string
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest, String max) {
        // API Key
        String str_key = "key=" + getString(R.string.direction_api_key);
        // Origin of route
        String str_origin = "from=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "to=" + dest.latitude + "," + dest.longitude;
        // routeType
        String str_type = "routeType=" + "pedestrian";
        // maxRoute set to 3
        String str_max = "maxRoutes=" + max;
        // Building the parameters to the web service
        String parameters = str_key + "&" + str_origin + "&" + str_dest + "&" + str_type + "&" + str_max;

        // Building the url to the web service
        String final_url = url + "?" + parameters;

        return final_url;
    }

    /**
     * Async worker for handling and parsing API json response, see DirectionsJSONParser Class for more info
     * After parsing, returned with data structure
     *
     * | { route1 distance, route2 distance, route3 distance }                                                                            |
     * | { [ (route1)SWBounding LatLng, NEBounding LatLng, origin LatLng, firstWayPoint LatLng, ..... lastWayPointB4Destination LatLng]     |
     * |   [ (route2)SWBounding LatLng, NEBounding LatLng, origin LatLng, firstWayPoint LatLng, ..... lastWayPointB4Destination LatLng]     |
     * |   [ (route3)SWBounding LatLng, NEBounding LatLng, origin LatLng, firstWayPoint LatLng, ..... lastWayPointB4Destination LatLng] }   |
     *
     * OnPostExecute construct polyline using the wayPoints of each route,
     * moves camera to be bounded with SWBounding LatLng, NEBounding LatLng of first route
     */
    private class parseDrawWorker extends AsyncTask<JSONObject , Void, List<List<List<LatLng>>> > {
        @Override
        protected List<List<List<LatLng>>> doInBackground(JSONObject... jsonObjects) {
            JSONObject response = jsonObjects[0];
            DirectionsJSONParser parser = new DirectionsJSONParser();
            return parser.parse(response);
        }
        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }
        protected void onPostExecute(List<List<List<LatLng>>> dataWrapper) {
            // First field in wrapper is distances of each route
            Log.i(TAG, "distance list " + dataWrapper.get(0).toString());
            // Second field in wrapper is list of routes, each route contains a list of wayPoints
            List<List<LatLng>> routes =  dataWrapper.get(1);
            Random rnd = new Random(1);
            for (int i = 0; i< routes.size(); i++){
                List<LatLng> route = routes.get(i);
                // use the first route's bounds to set map box
                if(i == 0 && moveCam){
                    // move camera and bounding box so account for nav bar at bottom
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                            new LatLngBounds(route.remove(0),route.remove(0)), 280));// second remove get's LatLng NEBound
                }
                else{
                    // don't care about bound box for other routes
                    route.remove(0);
                    route.remove(0);
                }
                // add polyLine with random color with the LatLng points received in routes
                mMap.addPolyline(new PolylineOptions().clickable(true).color(colors[i]).addAll(route));
            }
        }
    }

}


