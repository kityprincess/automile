package com.example.kityp.firebaseauth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CategoriesActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText category_editText;
    Button addCategory_button;
    Spinner categories_spinner;

    DatabaseReference databaseProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        databaseProfile = FirebaseDatabase.getInstance().getReference("profiles");

        category_editText = (EditText) findViewById(R.id.category_editText);
        addCategory_button = (Button) findViewById(R.id.addCategory_button);
        categories_spinner = (Spinner) findViewById(R.id.categories_spinner);

        addCategory_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void addCategory() {
        String category = category_editText.getText().toString().trim();

        if(!TextUtils.isEmpty(category)) {
            //TODO get user_uid from logged in user
            String user_uid = databaseProfile.push().getKey();


        }else {
            Toast.makeText(this, "Please enter a custom category.", Toast.LENGTH_SHORT).show();
        }
    }

}
