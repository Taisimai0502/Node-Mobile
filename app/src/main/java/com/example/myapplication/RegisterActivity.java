package com.example.myapplication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Login {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText usernameEditText = findViewById(R.id.registerUsername);
        final EditText passwordEditText = findViewById(R.id.registerPassword);
        final Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            SharedPreferences preferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();


            editor.putString(username, password);
            editor.apply();


            Intent intent = new Intent(RegisterActivity.this, Login.class);
            startActivity(intent);
        });

        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, Login.class);
            startActivity(intent);
        });
    }
}
