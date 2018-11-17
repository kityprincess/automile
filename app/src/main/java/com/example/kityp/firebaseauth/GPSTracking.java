//https://www.androidauthority.com/create-a-gps-tracking-application-with-firebase-realtime-databse-844343/
package com.example.kityp.firebaseauth;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class GPSTracking extends Service {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    String pauseTime;

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
        DatabaseReference databaseProfile = FirebaseDatabase.getInstance().getReference("profiles")
                .child(user_uid).child("pause_time");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            //TODO: SetInterval equal to pause time
            fusedLocationProviderClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();

            Log.e("GPSTracking", "requesting permission");

            databaseProfile.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pauseTime = dataSnapshot.getValue().toString();
                    Log.e("GPS EventListener", "Pause Time: " + pauseTime);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            // Time in miliseconds
            Log.e("outside EventListener", "Pause Time:" + pauseTime + ".");
            //log above will display number, line below displays error:
            // java.lang.RuntimeException: Unable to create service com.example.kityp.firebaseauth.GPSTracking: java.lang.NumberFormatException: null
            //long msPauseTime = Long.parseLong(pauseTime);
            //msPauseTime *= 60000;
            //Log.e("GPS EventListener", "MilliPause Time: " + msPauseTime);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(120000);
            locationRequest.setInterval(120000);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    DatabaseReference databaseGPS = FirebaseDatabase.getInstance().getReference("gps").child(user_uid);

                    Double latitude = locationResult.getLastLocation().getLatitude();
                    Double longitutde = locationResult.getLastLocation().getLongitude();
                    Long time = locationResult.getLastLocation().getTime();

                    if (latitude != null && longitutde != null) {
                        databaseGPS.child("start_lat").setValue(latitude);
                        databaseGPS.child("start_long").setValue(longitutde);
                        databaseGPS.child("start_time").setValue(time);
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


