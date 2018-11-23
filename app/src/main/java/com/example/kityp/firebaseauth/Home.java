package com.example.kityp.firebaseauth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    TextView textView;

    DatabaseReference databaseProfile;
    DatabaseReference databaseGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //databaseProfile = FirebaseDatabase.getInstance().getReference("profiles");

        Button start_button = (Button) findViewById(R.id.start_button);
        Button stop_button = (Button) findViewById(R.id.stop_button);
        textView = (TextView) findViewById(R.id.textView);


        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPermissions();
            }
        });

        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopGPSTracking();
            }
        });
    }


        public void callPermissions() {
            Log.e("Home Activity", "callPermissions");
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            String rationale = "GPS Tracking - Please provide location permission to allow AutoMile to track your mileage.";
            Permissions.Options options = new Permissions.Options()
                    .setRationaleDialogTitle("Info")
                    .setSettingsDialogTitle("Warning");

            Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
                @Override
                public void onGranted() {
                    Log.e("Home Activity", "callPermissions - on Granted");
//                    addNewTrip();
                    startGPSTracking();
                }

                @Override
                public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                    Log.e("Home Activity", "callPermissions - on denied");
                    super.onDenied(context, deniedPermissions);

                    callPermissions();
                }
            });

        }

//    private void addNewTrip() {
//        //TODO: all default values should be mull except uid
//            String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            Double cost = 54.5;
//
//            databaseGPS = FirebaseDatabase.getInstance().getReference("gps");
//
//            //TODO remove - for debugging purposes only
//            Log.e("Home Activity", "addNewTrip called");
//
//            Trip trip = new Trip(cost);
//
//            final String key = databaseGPS.child(user_uid).push().getKey();
//            databaseGPS.child(user_uid).push().setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//
//                        Intent intent = new Intent(Home.this, GPSTracking.class);
//                        intent.putExtra("KEY", key);
//                        startActivity(intent);
//
//                        startGPSTracking();
//                        Toast.makeText(Home.this, "Trip Started", Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(Home.this, "Problem Starting Trip", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
    private void startGPSTracking() {
            startService(new Intent(this, GPSTracking.class));
        }

    private void stopGPSTracking() {
        //TODO this isn't stopping the service
        stopService(new Intent(this, GPSTracking.class));
        Toast.makeText(Home.this, "Trip Recorded", Toast.LENGTH_SHORT).show();
        Log.e("Stop GPS Tracking", "Tracking Stopped");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuProfile:
                startActivity(new Intent(this, CreateProfile.class));
                break;

            case R.id.menuLogout:
                stopGPSTracking();
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));

                break;
        }

        return true;
    }
}
