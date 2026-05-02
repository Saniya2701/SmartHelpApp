package com.example.smarthelpapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthelpapp.R;
import com.google.firebase.auth.*;

public class ProfileActivity extends AppCompatActivity {

    TextView nameText, emailText;
    EditText editName, newPassword;
    Button updateNameBtn, changePassBtn, logoutBtn;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        editName = findViewById(R.id.editName);
        newPassword = findViewById(R.id.newPassword);

        updateNameBtn = findViewById(R.id.updateNameBtn);
        changePassBtn = findViewById(R.id.changePassBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            nameText.setText("Name: " + user.getDisplayName());
            emailText.setText("Email: " + user.getEmail());
        }

        // UPDATE NAME
        updateNameBtn.setOnClickListener(v -> {
            UserProfileChangeRequest request =
                    new UserProfileChangeRequest.Builder()
                            .setDisplayName(editName.getText().toString())
                            .build();

            user.updateProfile(request).addOnSuccessListener(unused ->
                    Toast.makeText(this, "Name Updated", Toast.LENGTH_SHORT).show());
        });

        // CHANGE PASSWORD
        changePassBtn.setOnClickListener(v -> {
            user.updatePassword(newPassword.getText().toString())
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Password Updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // LOGOUT
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}