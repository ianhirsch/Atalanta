package com.example.atalanta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class GenerateDistance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_distance);

        EditText Miles = (EditText) findViewById(R.id.mileage);
        String miles = Miles.getText().toString();


    }

    public void switchToDestination(View view) {
        Intent intent = new Intent(this, GenerateDestination.class);
        startActivity(intent);
    }

    public void generateDistance(View view) {
        //Save location
        //Pass location to generate path on MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onBackToMainButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}