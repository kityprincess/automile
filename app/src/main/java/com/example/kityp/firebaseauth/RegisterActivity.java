//https://github.com/probelalkhan/firebase-authentication-tutorial
package com.example.kityp.firebaseauth;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    ProgressBar progressBar;
    EditText email_editText, password_editText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_editText = (EditText) findViewById(R.id.email_editText);
        password_editText = (EditText) findViewById(R.id.password_editText);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.register_button).setOnClickListener(this);
        findViewById(R.id.login_textView).setOnClickListener(this);
    }

    private void registerUser() {
        Log.d("Register Activity", "inside register user");
        String email = email_editText.getText().toString().trim();
//        String email = "a@a.com";
//        String password = "123456";
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
            password_editText.setError("Password must be at least 6 characters");
            password_editText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "User registered", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(RegisterActivity.this, CreateProfile.class));
//                    finish();
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Email already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                registerUser();
                break;

            case R.id.login_textView:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}