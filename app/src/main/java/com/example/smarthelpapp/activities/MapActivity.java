package com.example.smarthelpapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.smarthelpapp.MainActivity;
import com.example.smarthelpapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private Button btnAll, btnHospital, btnMechanic, btnElectric, btnPlumber, btnPolice, shareBtn;
    private Button homeBtn, contactsBtn, mapBtn;

    private double currentLat = 0.0, currentLng = 0.0;
    private boolean isMapReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Top buttons
        btnAll = findViewById(R.id.btnAll);
        btnHospital = findViewById(R.id.btnHospital);
        btnMechanic = findViewById(R.id.btnMechanic);
        btnElectric = findViewById(R.id.btnElectric);
        btnPlumber = findViewById(R.id.btnPlumber);
        btnPolice = findViewById(R.id.btnPolice);
        shareBtn = findViewById(R.id.shareLocationBtn);

        // Bottom navigation
        homeBtn = findViewById(R.id.homeBtn);
        contactsBtn = findViewById(R.id.contactsBtn);
        mapBtn = findViewById(R.id.mapBtn);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Filters
        btnAll.setOnClickListener(v -> { if (isMapReady) showAllHelpers(); });
        btnHospital.setOnClickListener(v -> { if (isMapReady) showHospital(); });
        btnMechanic.setOnClickListener(v -> { if (isMapReady) showMechanic(); });
        btnElectric.setOnClickListener(v -> { if (isMapReady) showElectric(); });
        btnPlumber.setOnClickListener(v -> { if (isMapReady) showPlumber(); });
        btnPolice.setOnClickListener(v -> { if (isMapReady) showPolice(); });

        // Share location
        shareBtn.setOnClickListener(v -> {
            if (currentLat != 0.0) {
                String link = "https://maps.google.com/?q=" + currentLat + "," + currentLng;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "My Location: " + link);

                startActivity(Intent.createChooser(intent, "Share via"));
            } else {
                Toast.makeText(this, "Location not ready", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigation
        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this, MainActivity.class));
            finish();
        });

        contactsBtn.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this, ContactsActivity.class));
            finish();
        });

        mapBtn.setOnClickListener(v -> {
            // Already here
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        getUserLocation();
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                showAllHelpers();
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // USER MARKER (RED)
    private void addUserMarker() {
        LatLng loc = new LatLng(currentLat, currentLng);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

        mMap.addMarker(new MarkerOptions()
                .position(loc)
                .title("You (Current Location)")
                .snippet("📍 You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    // SHOW METHODS
    private void showAllHelpers() {
        mMap.clear();
        addUserMarker();

        addHospital();
        addMechanic();
        addElectric();
        addPlumber();
        addPolice();
    }

    private void showHospital() {
        mMap.clear();
        addUserMarker();
        addHospital();
    }

    private void showMechanic() {
        mMap.clear();
        addUserMarker();
        addMechanic();
    }

    private void showElectric() {
        mMap.clear();
        addUserMarker();
        addElectric();
    }

    private void showPlumber() {
        mMap.clear();
        addUserMarker();
        addPlumber();
    }

    private void showPolice() {
        mMap.clear();
        addUserMarker();
        addPolice();
    }

    // HELPERS
    private void addHospital() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLat + 0.002, currentLng))
                .title("City Hospital 🏥")
                .snippet("📞 9876501234")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }

    private void addMechanic() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLat - 0.002, currentLng))
                .title("Ajay Mechanic 🔧")
                .snippet("📞 9123456780")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }

    private void addElectric() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLat, currentLng + 0.002))
                .title("Rahul Electrician ⚡")
                .snippet("📞 9988776655")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }

    private void addPlumber() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLat + 0.003, currentLng))
                .title("Ramesh Plumber 🚰")
                .snippet("📞 9876543210")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }

    private void addPolice() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLat - 0.003, currentLng))
                .title("Police Station 🚓")
                .snippet("📞 100")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
    }
}