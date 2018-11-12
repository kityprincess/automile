//https://www.youtube.com/playlist?list=PLGCjwl1RrtcSi2oV5caEVScjkM6r3HO9t
package com.example.kityp.firebaseauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PauseTimeActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText pauseTime_editText;
    Button updatePauseTime_button;
    TextView pauseTime_textView;

    private DatabaseReference databaseProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause_time);

        Toolbar toolbar = findViewById(R.id.toolbar);

        String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseProfile = FirebaseDatabase.getInstance().getReference("profiles")
                .child(user_uid).child("pause_time");

        pauseTime_editText = (EditText) findViewById(R.id.pauseTime_editText);
        updatePauseTime_button = (Button) findViewById(R.id.updatePauseTime_button);
        pauseTime_textView = (TextView) findViewById(R.id.pauseTime_textView);

        databaseProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pauseTime = dataSnapshot.getValue().toString();
                Log.d("EventListener", "Pause Time");
                pauseTime_textView.setText("Pause Time: " + pauseTime);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updatePauseTime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePauseTime();
            }
        });
    }

    private void updatePauseTime() {
        String newPauseTime = pauseTime_editText.getText().toString().trim();

        if(!TextUtils.isEmpty(newPauseTime)) {
            databaseProfile.setValue(newPauseTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //TODO Clear the field
                        Toast.makeText(PauseTimeActivity.this, "Pause Time Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PauseTimeActivity.this, "Error updating Pause Time", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this, "Please enter a new category.", Toast.LENGTH_SHORT).show();
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
