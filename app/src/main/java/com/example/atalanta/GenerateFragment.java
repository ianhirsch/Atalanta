package com.example.atalanta;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class GenerateFragment extends Fragment{
    private View view;
    private Button health, profile;
    private Spinner miles;
    private Integer[] mileOptions;
    private static String PREF_NAME = "com.example.atalanta";
    public static Integer selectedMileage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate layout for this fragment
        view = inflater.inflate(R.layout.generate_fragment, container, false);

        //Get reference of button
        health = (Button) view.findViewById(R.id.navHealth);
        //Perform setOnClickListener on first button
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_health = new Intent(getActivity(), HealthActivity.class);
                startActivity(intent_health);
            }
        });

        profile = (Button) view.findViewById(R.id.navProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        mileOptions = new Integer[20];
        for(int i = 1; i <= 20; i++){
            mileOptions[i-1] = i;
        }
        Spinner miles = (Spinner) view.findViewById(R.id.milesNum);
        miles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMileage = Integer.parseInt(parent.getItemAtPosition(position).toString());
                //To store mileage value in shared preferences
                setMileage(getActivity().getApplicationContext(), selectedMileage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMileage = 1;
                //To store mileage value in shared preferences
                setMileage(getActivity().getApplicationContext(), selectedMileage);
            }
        });

        //To retrieve mileage value in shared preferences
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Integer miles = preferences.getInt("Mileage", 0);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this.getActivity(), android.R.layout.simple_spinner_item, mileOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        miles.setAdapter(adapter);

        return view;
    }

    private static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setMileage(Context context, Integer input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("Mileage", input).apply();
    }

}