package com.example.atalanta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void onSignupButtonClick(View view) {
        boolean valid = true;
        EditText usernameText = (EditText) findViewById(R.id.signup_username);
        EditText emailText = (EditText) findViewById(R.id.signup_email);
        EditText passwordText = (EditText) findViewById(R.id.signup_password);
        String str1 = usernameText.getText().toString();
        String str2 = emailText.getText().toString();
        String str3 = passwordText.getText().toString();


        // validate input
        if (str1.isEmpty() || str1.length() < 3) {
            usernameText.setError("at least 3 characters");
            valid = false;
        } else {
            usernameText.setError(null);
        }
        if (str2.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(str2).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }
        if (str3.isEmpty() || str3.length() < 2 || str3.length() > 10) {
            passwordText.setError("between 2 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if(valid)
        {
            //store key as username in SharedPreference
            SharedPreferences sharedPreferences = getSharedPreferences("com.example.atalanta", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("username",str1).apply();
            sharedPreferences.edit().putString("email",str2).apply();
            sharedPreferences.edit().putString("password",str3).apply();
            sharedPreferences.edit().putBoolean("loggedIn",true).apply();
            //go to main activity
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    public void onlogBackInButtonClick(View view) {
        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
