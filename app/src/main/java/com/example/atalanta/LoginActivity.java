package com.example.atalanta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginButtonClick(View view) {
        boolean valid = true;
        EditText emailText = (EditText) findViewById(R.id.email);
        EditText passwordText = (EditText) findViewById(R.id.password);
        String str = emailText.getText().toString();
        String str2 = passwordText.getText().toString();

        // validate input
        if (str.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }
        if (str2.isEmpty() || str2.length() < 2 || str2.length() > 10) {
            passwordText.setError("between 2 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if(valid)
        {
            //store key as username in SharedPreference
            SharedPreferences sharedPreferences = getSharedPreferences("com.example.atalanta", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("email",str).apply();
            sharedPreferences.edit().putString("password",str2).apply();
            //go to main activity
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    public void onSignupButtonClick(View view) {
        Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
        LoginActivity.this.startActivity(intent);
    }
}
