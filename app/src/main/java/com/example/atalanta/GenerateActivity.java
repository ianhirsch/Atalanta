package com.example.atalanta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GenerateActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private EditText mDistText;
    private EditText mDestText;
    private String   inputFromUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        findViewById(R.id.distanceButton).setOnClickListener(this);
        findViewById(R.id.destinationButton).setOnClickListener(this);
        mDistText = findViewById(R.id.distInput);
        mDestText = findViewById(R.id.destInput);
    }
    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.distanceButton:
                mDistText.setVisibility(View.VISIBLE);
                mDestText.setVisibility(View.GONE);
                break;
            case R.id.destinationButton:
                mDistText.setVisibility(View.GONE);
                mDestText.setVisibility(View.VISIBLE);
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.container_frame_back, new MapFragment());
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                break;
            case R.id.goButton:
                // user chose distance
                if(mDistText.getVisibility() == View.VISIBLE)
                {
                    inputFromUser = mDistText.getText().toString();
                }
                // user chose distance
                else if(mDestText.getVisibility() == View.VISIBLE)
                {
                    inputFromUser = mDestText.getText().toString();
                }
                //
        }
    }
    public void onBackToMainButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.container_frame_back, fragment, tag);
        ft.commitAllowingStateLoss();
    }
}