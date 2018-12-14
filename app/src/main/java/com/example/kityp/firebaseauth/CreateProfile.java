//https://github.com/probelalkhan/firebase-authentication-tutorial
//https://www.youtube.com/watch?v=GtxVILjLcw8&feature=youtu.be
package com.example.kityp.firebaseauth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Date;

public class CreateProfile extends AppCompatActivity implements View.OnClickListener {

    TextView textView;
    EditText displayName_editText;
    EditText emailAddress_editText;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    private DatabaseReference databaseProfile;
    private DatabaseReference databaseName;
    private DatabaseReference databaseEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        mAuth = FirebaseAuth.getInstance();
        String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseProfile = FirebaseDatabase.getInstance().getReference("profiles")
                .child(user_uid);
        databaseName = databaseProfile.child("displayName");
        databaseEmail = databaseProfile.child("emailAdress");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayName_editText = (EditText) findViewById(R.id.displayName_editText);
        emailAddress_editText = (EditText) findViewById(R.id.emailAddress_editText);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        Button wrkHours_button = findViewById(R.id.wrkHours_button);
        Button categories_button = findViewById(R.id.categories_button);
        Button pauseTime_button = findViewById(R.id.pauseTime_button);
//        Button scheduleReports_button = findViewById(R.id.scheduleReports_button);

        loadUserInformation();

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
// TODO: Add hints or instructions to each setting below
        wrkHours_button.setOnClickListener(this);
        categories_button.setOnClickListener(this);
        pauseTime_button.setOnClickListener(this);
//        scheduleReports_button.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void loadUserInformation() {
        databaseName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.getValue(String.class);
                Log.e("EventListener", "Display Name" + displayName);
                displayName_editText.setText(displayName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseEmail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String emailAddress = dataSnapshot.getValue(String.class);
                Log.e("EventListener", "Display Name" + emailAddress);
                emailAddress_editText.setText(emailAddress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        if (user != null) {
//            if (user.getDisplayName() != null) {
//                displayName_editText.setText(user.getDisplayName());
//            }
//        }
    }

    private void saveUserInformation() {

        String newDisplayName = displayName_editText.getText().toString().trim();
        String newEmailAddress = emailAddress_editText.getText().toString().trim();

        if (newDisplayName.isEmpty()) {
            displayName_editText.setError("Name required");
            displayName_editText.requestFocus();
            return;
        } else {
            databaseName.setValue(newDisplayName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateProfile.this, "Name Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateProfile.this, "Error updating Name", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (newEmailAddress.isEmpty()) {
            emailAddress_editText.setError("Email Address required");
            emailAddress_editText.requestFocus();
            return;
        } else {
            databaseEmail.setValue(newEmailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateProfile.this, "Email Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateProfile.this, "Error updating Email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wrkHours_button:
                Toast.makeText(this, "Work Hours Button Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.categories_button:
                //Toast.makeText(this, "Go To Categories Activity", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, CategoriesActivity.class));
                break;
            case R.id.pauseTime_button:
                //Toast.makeText(this, "Pause Time Button Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, PauseTimeActivity.class));
                break;
//            case R.id.scheduleReports_button:
//                Toast.makeText(this, "Schedule Reports Button Clicked", Toast.LENGTH_SHORT).show();
//                break;
        }

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
                Intent intentProfile = new Intent(this, CreateProfile.class);
                break;
            case R.id.menuHome:
                startActivity(new Intent(this, Home.class));
                break;
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

        return true;
    }
}