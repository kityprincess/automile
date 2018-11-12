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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Date;

public class CreateProfile extends AppCompatActivity implements View.OnClickListener {

    TextView textView;
    EditText editText;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.DisplayName_editText);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        Button WrkHours_button = findViewById(R.id.WrkHours_button);
        Button Categories_button = findViewById(R.id.Categories_button);
        Button PauseTime_button = findViewById(R.id.PauseTime_button);
        Button ScheduleReports_button = findViewById(R.id.ScheduleReports_button);

        loadUserInformation();

        findViewById(R.id.Save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
// TODO: Add hints or instructions to each setting below
        WrkHours_button.setOnClickListener(this);
        Categories_button.setOnClickListener(this);
        PauseTime_button.setOnClickListener(this);
        ScheduleReports_button.setOnClickListener(this);
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
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getDisplayName() != null) {
                editText.setText(user.getDisplayName());
            }
        }
    }


    private void saveUserInformation() {

        String displayName = editText.getText().toString();

        if (displayName.isEmpty()) {
            editText.setError("Name required");
            editText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CreateProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.WrkHours_button:
                Toast.makeText(this, "Work Hours Button Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Categories_button:
                //Toast.makeText(this, "Go To Categories Activity", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, CategoriesActivity.class));
                break;
            case R.id.PauseTime_button:
                //Toast.makeText(this, "Pause Time Button Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, PauseTimeActivity.class));
                break;
            case R.id.ScheduleReports_button:
                Toast.makeText(this, "Schedule Reports Button Clicked", Toast.LENGTH_SHORT).show();
                break;
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
                Intent intent = new Intent(this, CreateProfile.class);
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