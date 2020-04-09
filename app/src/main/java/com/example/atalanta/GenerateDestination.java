package com.example.atalanta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class GenerateDestination extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_destination);


    }

    public void switchToDistance(View view) {
        Intent intent = new Intent(this, GenerateDistance.class);
        startActivity(intent);
    }

    public void generateDestination(View view) {
        //Save locations
        //Pass locations to generate paths on MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onBackToMainButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}