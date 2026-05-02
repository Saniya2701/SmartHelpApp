package com.example.smarthelpapp.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.smarthelpapp.database.DatabaseHelper;
import com.example.smarthelpapp.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class SOSService {

    private static final String TAG = "SOSService";
    private Context context;
    private DatabaseHelper dbHelper;
    private SMSHelper smsHelper;
    private LocationHelper locationHelper;
    private SOSCallback callback;

    private boolean isSMSSent = false; // 🔥 IMPORTANT FLAG

    public interface SOSCallback {
        void onSOSStarted();
        void onLocationFound(Location location);
        void onSMSSent();
        void onError(String error);
    }

    public SOSService(Context context, SOSCallback callback) {
        this.context = context;
        this.callback = callback;
        this.dbHelper = new DatabaseHelper(context);
        this.smsHelper = new SMSHelper(context);
    }

    public void activateSOS() {
        Log.d(TAG, "SOS Activated!");
        callback.onSOSStarted();

        isSMSSent = false; // reset every time

        List<Contact> contactList = dbHelper.getAllContacts();
        if (contactList.isEmpty()) {
            callback.onError("No emergency contacts added. Please add contacts first.");
            return;
        }

        locationHelper = new LocationHelper(context, new LocationHelper.LocationCallback() {
            @Override
            public void onLocationReceived(Location location) {

                // 🚫 STOP MULTIPLE SMS
                if (isSMSSent) return;

                isSMSSent = true;

                Log.d(TAG, "Location received: " + location.getLatitude() + ", " + location.getLongitude());
                callback.onLocationFound(location);

                String mapsLink = LocationHelper.getGoogleMapsLink(
                        location.getLatitude(),
                        location.getLongitude()
                );

                String sosMessage = SMSHelper.createSOSMessage(mapsLink);

                List<String> phoneNumbers = new ArrayList<>();
                for (Contact contact : contactList) {
                    phoneNumbers.add(contact.getPhone());
                }

                smsHelper.sendSMSToMultiple(phoneNumbers, sosMessage);
                Log.d(TAG, "SMS sent to " + phoneNumbers.size() + " contacts");

                callback.onSMSSent();

                // ✅ STOP LOCATION AFTER FIRST SUCCESS
                locationHelper.stopLocationUpdates();
            }

            @Override
            public void onLocationError(String error) {
                Log.e(TAG, "Location error: " + error);

                if (!isSMSSent) {
                    isSMSSent = true;

                    // fallback SMS
                    String fallbackMessage = "🚨 EMERGENCY! I need help. Location not available.";

                    List<String> phoneNumbers = new ArrayList<>();
                    for (Contact contact : contactList) {
                        phoneNumbers.add(contact.getPhone());
                    }

                    smsHelper.sendSMSToMultiple(phoneNumbers, fallbackMessage);
                    callback.onSMSSent();
                }

                callback.onError(error);
            }
        });

        locationHelper.startLocationUpdates();
    }

    public void stopSOS() {
        if (locationHelper != null) {
            locationHelper.stopLocationUpdates();
        }
        Log.d(TAG, "SOS Stopped");
    }
}