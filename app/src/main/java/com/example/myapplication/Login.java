package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(view -> {
            String inputUsername = usernameEditText.getText().toString();
            String inputPassword = passwordEditText.getText().toString();

            SharedPreferences preferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
            String registeredPassword = preferences.getString(inputUsername, "");

            if (registeredPassword.equals(inputPassword)) {
                startActivity(new Intent(Login.this, MainActivity.class));
            } else {
                Toast.makeText(Login.this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
