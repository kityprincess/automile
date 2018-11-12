//https://www.androidauthority.com/create-a-gps-tracking-application-with-firebase-realtime-databse-844343/
package com.example.kityp.firebaseauth;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class GPSTracking extends Service {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loginToFirebase();
        callPermissions();
    }

    private void loginToFirebase() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //TODO how do you authenticate Realtime database with FirebaseAuth user?
        String password = "123456";

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult>task) {
                if (task.isSuccessful()) {
                    requestLocationUpdates();
                } else {
                    Log.d("GPS Service", "Firebase Authentication failed");
                }
            }
        });
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            //TODO: SetInterval equal to pause time
            fusedLocationProviderClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();

            Log.e("GPSTracking", "requesting permission");

            // TODO: get pause time from Firebase and use for setInterval
            // Time in miliseconds
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(2000);
            locationRequest.setInterval(5000);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    DatabaseReference databaseGPS = FirebaseDatabase.getInstance().getReference("gps");
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        databaseGPS.setValue(location);
                    }

                    //TODO Remove - debugging only
                    Log.d("GPS Tracking", "in requestLocationUpdates");
                    Log.d("CurrentLocation", "Lat: "+locationResult.getLastLocation().getLatitude() + "Long: "+locationResult.getLastLocation().getLongitude());
                }
            }, getMainLooper());
        } else callPermissions();
    }

    public void callPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        String rationale = "GPS Tracking - Please provide location permission to allow AutoMile to track your mileage.";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                requestLocationUpdates();
                Log.e("gpstracking", "permission granted");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);

                callPermissions();
            }
        });

    }
}


