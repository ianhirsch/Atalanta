package com.example.atalanta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onButtonClick(View view) {
        EditText usernameText = (EditText) findViewById(R.id.username);
        EditText passwordText = (EditText) findViewById(R.id.password);
        String str = usernameText.getText().toString();
        String str2 = passwordText.getText().toString();
        //store key as username in SharedPreference
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.atalanta", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("username",str).apply();
        sharedPreferences.edit().putString("password",str2).apply();
        //go to main activity
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
