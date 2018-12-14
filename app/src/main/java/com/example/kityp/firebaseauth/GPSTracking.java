//https://www.androidauthority.com/create-a-gps-tracking-application-with-firebase-realtime-databse-844343/
//http://www.northborder-software.com/location_provider_client_4.html
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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static android.app.PendingIntent.getActivity;
import static android.content.Intent.getIntent;

public class GPSTracking extends Service {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    //get user ID from authentication
    String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //gets current GPS DB record
    DatabaseReference databaseGPS = FirebaseDatabase.getInstance().getReference("gps").child(user_uid);
    //gets current profile DB record
    DatabaseReference databaseProfile = FirebaseDatabase.getInstance().getReference("profiles").child(user_uid);

    String key;
    double curLat = 0;
    double curLong = 0;
    long curTime = 0;
    double startLat = 0;
    double startLong;
    long startTime;
    double endLat;
    double endLong;
    long endTime;
    double cost;
    double mileage_rate;
    int pause_time;
    int count;
    long duration;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Stepping through", "Step 1: Initialize locationRequest");

        //60000 milliseconds = 1 minute
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(60000);
        locationRequest.setInterval(60000);

        databaseProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Stepping through", "Step 2: ValueEventListener");
                mileage_rate = dataSnapshot.child("mileage_rate").getValue(double.class);
                Log.e("Profile DB Listener", "Mileage Rate: " + mileage_rate);

                pause_time = dataSnapshot.child("pause_time").getValue(int.class);
                pause_time = pause_time--;
                Log.e("Profile DB Listener", "Pause Time: " + pause_time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        callPermissions();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.e("Stepping through", "Step 7: Location Callback");
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {

                    curLat = locationResult.getLastLocation().getLatitude();
                    curLong = locationResult.getLastLocation().getLongitude();
                    curTime = locationResult.getLastLocation().getTime();

                    Log.e("Location", "locationResult Starting Latitude: " + startLat);
                    Log.e("Location", "locationResult Starting Longitude: " + startLong);
                    Log.e("Location", "locationResult Current Latitude: " + curLat);
                    Log.e("Location", "locationResult Current Longitutde: " + curLong);


                    if (curLat != 0 && curLong != 0 && startLat == 0 && startLong == 0) {
                        //starting a trip - start & end values are null
                        Log.e("Location", "Just starting trip");

                        updateStartpoint();
                        updateEndpoint();
                        return;
                    } else if ((curLat == endLat || curLong == endLong) && count == pause_time) {
                        //pause time reached - close the trip and complete calculations
                        updatedEndTime();

                        duration = endTime - startTime;
                        databaseGPS.child(key).child("duration").setValue(duration);
                        Log.e("Location", "Ending a trip");
                        Log.e("Location", "Duration: " + duration);
                        initializeTrip();
                        onDestroy();
                        return;
                    } else if ((curLat == endLat || curLong == endLong) && count < pause_time) {
                        //driving paused but pause time not reached - update pause time & increment count
                        Log.e("Location", "trip paused");
                        updatedEndTime();
                        count++;
                        return;
                    } else if ((curLat != startLat || curLong != startLong || curLat != endLat || curLong != endLong) && endTime != 0) {
                        //trip started but driving not paused - update ends
                        Log.e("Location", "driving");
                        if (curLat == endLat) {
                            Log.e("Driving", "curLat == endLat");
                        }
                        count = 0;
                        updateEndpoint();
                        return;
                    }
                }
            }
        };
    }

    public void callPermissions() {
        Log.e("Stepping through", "Step 3: Call Permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        String rationale = "GPS Tracking - Please provide location permission to allow AutoMile to track your mileage.";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                loginToFirebase();
                Log.e("gpstracking", "permission granted");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);

                callPermissions();
            }
        });
    }

    private void loginToFirebase() {
        Log.e("Stepping through", "Step 4: Log into Firebase");
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //TODO how do you authenticate Realtime database with FirebaseAuth user?
        String password = "123456";

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult>task) {
                if (task.isSuccessful()) {
                    addNewTrip();
                } else {
                    Log.d("GPS Service", "Firebase Authentication failed");
                }
            }
        });
    }

    private void addNewTrip() {
        Log.e("Stepping through", "Step 5: Add New Trip");
        //TODO: all default values should be mull except uid
        //String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        double cost = 54.5;

        //TODO remove - for debugging purposes only
        Log.e("GPSTracking Activity", "addNewTrip called");

        Trip trip = new Trip(mileage_rate);

        key = databaseGPS.push().getKey();
        Log.e("addNewTrip", "key: " + key);

        databaseGPS.child(key).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //key = databaseGPS.getKey();

                    Toast.makeText(GPSTracking.this, "Trip Started", Toast.LENGTH_SHORT).show();
                    requestLocationUpdates();

                } else {
                    Toast.makeText(GPSTracking.this, "Problem Starting Trip", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestLocationUpdates() {
        Log.e("Stepping through", "Step 6: Request Location Updates");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

        } else callPermissions();
    }

    public void updateStartpoint() {
        startLat = curLat;
        startLong = curLong;
        startTime = curTime;

        databaseGPS.child(key).child("start_lat").setValue(startLat);
        databaseGPS.child(key).child("start_long").setValue(startLong);
        databaseGPS.child(key).child("start_time").setValue(startTime);

        Log.e("Location", " Update Start Point called: " + count);
        Log.e("Location", count + " write Starting Latitude: " + startLat);
        Log.e("Location", count + " write Starting Longitutde: " + startLong);
    }

    public void updateEndpoint() {
        endLat = curLat;
        endLong = curLong;
        endTime = curTime;

        Log.e("Location", " Update End Point called: " + count);
        databaseGPS.child(key).child("end_lat").setValue(endLat);
        databaseGPS.child(key).child("end_long").setValue(endLong);
        databaseGPS.child(key).child("end_time").setValue(endTime);

        Log.e("Location", count + " Ending Latitude: " + endLat);
        Log.e("Location", count + " Ending Longitute: " + endLong);
        Log.e("Location", count + " Ending Time: " + endTime);
    }

    private void updatedEndTime() {
        endTime = curTime;
        databaseGPS.child(key).child("end_time").setValue(endTime);
        Log.e("Location", count + " Ending Time: " + endTime);
    }

    private void initializeTrip() {
        key = null;
        curLat = 0;
        curLong = 0;
        curTime = 0;
        startLat = 0;
        startLong = 0;
        startTime = 0;
        endLat = 0;
        endLong = 0;
        endTime = 0;
        cost = 0;
        mileage_rate = 0;
        pause_time = 0;
        count = 0;
        duration = 0;
    }

    @Override
    public void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Toast.makeText(GPSTracking.this, "Trip Recorded", Toast.LENGTH_SHORT).show();
        Log.e("Stop GPS Tracking", "Tracking Stopped");
    }
}