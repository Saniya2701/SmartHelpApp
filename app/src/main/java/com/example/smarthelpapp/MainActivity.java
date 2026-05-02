package com.example.smarthelpapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smarthelpapp.activities.ContactsActivity;
import com.example.smarthelpapp.activities.MapActivity;
import com.example.smarthelpapp.activities.ProfileActivity;
import com.example.smarthelpapp.database.DatabaseHelper;
import com.example.smarthelpapp.models.Contact;
import com.example.smarthelpapp.utils.SOSService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button sosButton, contactsBtn, mapBtn, callBtn, profileBtn;
    private TextView userNameText;

    private SOSService sosService;
    private DatabaseHelper dbHelper;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sosButton = findViewById(R.id.sosButton);
        contactsBtn = findViewById(R.id.contactsBtn);
        mapBtn = findViewById(R.id.mapBtn);
        callBtn = findViewById(R.id.callBtn);
        profileBtn = findViewById(R.id.profileBtn);
        userNameText = findViewById(R.id.userNameText);

        dbHelper = new DatabaseHelper(this);

        requestPermissions();

        // 🔥 Firebase user display
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userNameText.setText("👋 Welcome " + user.getDisplayName());
        }

        sosService = new SOSService(this, new SOSService.SOSCallback() {
            @Override
            public void onSOSStarted() {
                Toast.makeText(MainActivity.this, "🚨 SOS ACTIVATED!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLocationFound(Location location) {
                Toast.makeText(MainActivity.this,
                        "📍 Location: " + location.getLatitude() + ", " + location.getLongitude(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSMSSent() {
                Toast.makeText(MainActivity.this, "✅ SOS sent!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "❌ " + error, Toast.LENGTH_LONG).show();
            }
        });

        // SOS Button
        sosButton.setOnClickListener(v -> sosService.activateSOS());

        // Emergency Call
        callBtn.setOnClickListener(v -> makeEmergencyCall());

        // Contacts
        contactsBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ContactsActivity.class));
        });

        // Map
        mapBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        });

        // 🔥 PROFILE BUTTON
        profileBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
    }

    private void makeEmergencyCall() {

        List<Contact> contacts = dbHelper.getAllContacts();

        if (contacts.isEmpty()) {
            Toast.makeText(this, "No emergency contacts!", Toast.LENGTH_SHORT).show();
            return;
        }

        String phone = contacts.get(0).getPhone();

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }
}