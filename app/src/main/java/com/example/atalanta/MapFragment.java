package com.example.atalanta;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private final LatLng googlplex = new LatLng(37.4220, -122.0841); // google plex
    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        // Location service
         mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                displayMyLocation(); // called in getMapAsync since want map ready then display current marker
                mMap.clear();

            }
        });
        return rootView;
    }
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
        int permission = ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        // if not, ask for permission
        if(permission == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // if granted, display marker at current location
        else
        {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(getActivity(),task ->
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

    public void placeMarker(String title, double lat, double lon) {
        if (mMap != null) {
            LatLng marker = new LatLng(lat, lon);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
            mMap.addMarker(new MarkerOptions().title(title).position(marker));
        }
    }
}