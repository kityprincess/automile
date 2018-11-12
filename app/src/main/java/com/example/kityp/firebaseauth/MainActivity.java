//https://github.com/probelalkhan/firebase-authentication-tutorial

package com.example.kityp.firebaseauth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kityp.firebaseauth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    EditText email_editText, password_editText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        email_editText = (EditText) findViewById(R.id.email_editText);
        password_editText = (EditText) findViewById(R.id.password_editText);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        findViewById(R.id.register_textView).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

    }

    private void userLogin() {
        String email = email_editText.getText().toString().trim();
        String password = password_editText.getText().toString().trim();

        if (email.isEmpty()) {
            email_editText.setError("Email is required");
            email_editText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_editText.setError("Please enter a valid email");
            email_editText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            password_editText.setError("Password is required");
            password_editText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            password_editText.setError("Minimum passsword length of 6");
            password_editText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);



        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                        Log.d("Main Activity", "checking permissions");
                    } else callPermissions();
                    finish();
                    //Intent intent = new Intent(MainActivity.this, CreateProfile.class);
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
                finish();
                startGPSTracking();
                //Intent intent = new Intent(MainActivity.this, CreateProfile.class);
                Intent intent = new Intent(MainActivity.this, Home.class);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_textView:
                finish();
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.login_button:
                userLogin();
                break;
        }
    }

    public void callPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        String rationale = "Please provide location permission to allow AutoMile to track your mileage.";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                Log.e("main activity", "permission granted");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                Log.e("main activity", "permission denied");
            }
        });
    }

    private void startGPSTracking() {
        startService(new Intent(this, GPSTracking.class));
    }
}
