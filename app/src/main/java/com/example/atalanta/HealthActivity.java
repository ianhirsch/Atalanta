package com.example.atalanta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class HealthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        EditText TargetHeartRate = (EditText) findViewById(R.id.TargetHeartRate);
        String HrtRate = TargetHeartRate.getText().toString();

    }

    public void onBackToMainButtonClick(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
