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
import android.widget.Toast;

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

import static android.app.PendingIntent.getActivity;
import static android.content.Intent.getIntent;

public class GPSTracking extends Service {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

//    Intent intent = getIntent();

    String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference databaseGPS = FirebaseDatabase.getInstance().getReference("gps").child(user_uid);

    String key = null;
    Double curLat = null;
    Double curLong = null;
    Long curTime = null;
    Double startLat = null;
    Double startLong = null;
    Long startTime = null;
    Double endLat = null;
    Double endLong = null;
    Long endTime = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
                    addNewTrip();
                } else {
                    Log.d("GPS Service", "Firebase Authentication failed");
                }
            }
        });
    }


    private void addNewTrip() {
        //TODO: all default values should be mull except uid
        String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Double cost = 54.5;

        //TODO remove - for debugging purposes only
        Log.e("GPSTracking Activity", "addNewTrip called");

        Trip trip = new Trip(cost);


        databaseGPS.push().setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    key = databaseGPS.push().getKey();
                    Toast.makeText(GPSTracking.this, "Trip Started", Toast.LENGTH_SHORT).show();
                    requestLocationUpdates();

                } else {
                    Toast.makeText(GPSTracking.this, "Problem Starting Trip", Toast.LENGTH_SHORT).show();
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

            //60000 milliseconds = 1 second
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(60000);
            locationRequest.setInterval(60000);

            databaseGPS.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    startLat = dataSnapshot.child(key).child("start_lat").getValue(Double.class);

                    Log.e("Location", "Listener Starting Latitude: " + startLat);
                    Log.e("Location", "Listener Starting Longitude: " + startLong);
                    Log.e("Location", "Listener Current Latitude: " + curLat);
                    Log.e("Location", "Listener Current Longitutde: " + curLong);
                    Log.e("Location", "Listener Current Time: " + curTime);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    curLat = locationResult.getLastLocation().getLatitude();
                    curLong = locationResult.getLastLocation().getLongitude();
                    curTime = locationResult.getLastLocation().getTime();

                    Log.e("Location", "locationResult Starting Latitude: " + startLat);
                    Log.e("Location", "locationResult Starting Longitude: " + startLong);
                    Log.e("Location", "locationResult Current Latitude: " + curLat);
                    Log.e("Location", "locationResult Current Longitutde: " + curLong);

                    //TODO at starting Longitude after figuring out why it's taking a value too early
                    if (curLat != null && curLong != null && startLat == null) {
                        Log.e("Location", "Are we even making it through the ifs ands buts");
                        databaseGPS.child(key).child("start_lat").setValue(startLat);
                        databaseGPS.child(key).child("start_long").setValue(startLong);
                        databaseGPS.child(key).child("start_time").setValue(startTime);
                        databaseGPS.child(key).child("end_lat").setValue(endLat);
                        databaseGPS.child(key).child("end_long").setValue(endLong);
                        databaseGPS.child(key).child("end_time").setValue(endTime);
                        startLat = curLat;
                        startLong = curLong ;
                        startTime = curTime;

                        Log.e("Location", "First write Starting Latitude: " + startLat);
                        Log.e("Location", "First write Starting Longitutde: " + startLong);
                        Log.e("Location", "First write Current Latitude: " + curLat);
                        Log.e("Location", "First write Current Longitutde: " + curLong);
                        Log.e("Location", "First write Current Time: " + curTime);
                    }

                    //TODO Remove - debugging only
                    Log.d("GPS Tracking", "in requestLocationUpdates");
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
}


