package com.example.smarthelpapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthelpapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, password;
    Button signupBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn);

        auth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(v -> {

            String n = name.getText().toString().trim();
            String e = email.getText().toString().trim();
            String p = password.getText().toString().trim();

            if (n.isEmpty() || e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(e, p)
                    .addOnSuccessListener(authResult -> {

                        FirebaseUser user = auth.getCurrentUser();

                        UserProfileChangeRequest profile =
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(n)
                                        .build();

                        user.updateProfile(profile);

                        Toast.makeText(this, "Signup Success", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this, LoginActivity.class));
                        finish();

                    })
                    .addOnFailureListener(e1 ->
                            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}