package com.example.atalanta;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Fragment;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ListIterator;
import java.util.Random;


public class MainActivity extends FragmentActivity {
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private RequestQueue requestQueue;
    private String url = "https://www.mapquestapi.com/directions/v2/alternateroutes";
    private static final String TAG = "ApiRequest";
    private final LatLng mDestinationLatLng = new LatLng(37.3349, -122.0091); // apple park
    private final LatLng googlplex = new LatLng(37.4220, -122.0841); // google plex
    private final int[] colors = {Color.RED,Color.BLUE,Color.DKGRAY};
    Button test_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Display google maps in fragment
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            displayMyLocation(); // called in getMapAsync since want map ready then display current marker
        });
        loadFragment(new GenerateFragment());

        // Test button for accessing login page, navbar onclick implemented
        test_button = (Button) findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                mMap.addMarker(new MarkerOptions().position(mDestinationLatLng));
                sendAndRequestResponse(mDestinationLatLng);
            }
        });


    }

    /**
     * Handles the result of the request for location permissions
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]grantResults)
    {
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        {
            // if request is cancelled, result arrays are empty
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                displayMyLocation();
        }
    }
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
                    LatLng mLastKnownLatLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                    // move camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mLastKnownLatLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                    // add current position marker
                    mMap.addMarker(new MarkerOptions().position(mLastKnownLatLng));
                }
            });
        }
    }

    // function to get route direction
    private void sendAndRequestResponse(LatLng dest) {
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
                    //RequestQueue initialized
                    requestQueue = Volley.newRequestQueue(this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, getDirectionsUrl(mLastKnownLatLng,dest), null, new Response.Listener<JSONObject>() {
                                // use googleplex as backup origin
                                @Override
                                public void onResponse(JSONObject response) {
                                    DirectionsJSONParser parser = new DirectionsJSONParser();
                                    // response is json
                                    List<List<LatLng>> routes = parser.parse(response);
                                    Random rnd = new Random(1);
                                    for (int i = 0; i< routes.size(); i++){
                                        List<LatLng> route = routes.get(i);
                                        // use the first route's bounds to set map box
                                        if(i == 0){
                                            // move camera and bounding box so account for nav bar at bottom
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                                                    new LatLngBounds(route.remove(0),route.remove(0)), 200));// second remove get's LatLng NEBound
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
                            }, error -> Log.d(TAG,"Error: "+ error.toString()));
                    requestQueue.add(jsonObjectRequest);
                }
            });
        }

    }
    // helper function for constructing the API query
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // API Key
        String str_key = "key=" + getString(R.string.direction_api_key);
        // Origin of route
        String str_origin = "from=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "to=" + dest.latitude + "," + dest.longitude;
        // routeType
        String str_type = "routeType=" + "pedestrian";
        // maxRoute set to 3
        String str_max = "maxRoutes=" + "3";
        // Building the parameters to the web service
        String parameters = str_key + "&"+ str_origin + "&" + str_dest + "&" + str_type + "&" + str_max;

        // Building the url to the web service
        String final_url = url + "?" + parameters;

        return final_url;
    }
    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace theFragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }
    private class drawRoute extends AsyncTask<List<LatLng>, Void, Void> {
        @Override
        protected Void doInBackground(List<LatLng> ... passing) {
            List<LatLng> list = passing[0];
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.clickable(true).color(Color.RED).addAll(list);
            mMap.addPolyline(polylineOptions);
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
//            showDialog("Downloaded " + result + " bytes");
        }
    }
}


