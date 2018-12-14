//https://www.youtube.com/playlist?list=PLGCjwl1RrtcSi2oV5caEVScjkM6r3HO9t
package com.example.kityp.firebaseauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {

    EditText category_editText;
    Button addCategory_button;
    Spinner categories_spinner;
    ListView categories_listView;

    private DatabaseReference databaseProfile;
    private ArrayList<String> existingCategories = new ArrayList<>();
    private ArrayList<String> categoriesKeys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseProfile = FirebaseDatabase.getInstance().getReference("profiles")
                .child(user_uid).child("categories");

        category_editText = (EditText) findViewById(R.id.category_editText);
        addCategory_button = (Button) findViewById(R.id.addCategory_button);
        categories_spinner = (Spinner) findViewById(R.id.categories_spinner);
        categories_listView = (ListView) findViewById(R.id.categories_listView);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, existingCategories);

        categories_listView.setAdapter(arrayAdapter);

        databaseProfile.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String category = dataSnapshot.getValue(String.class);
                existingCategories.add(category);

                String key = dataSnapshot.getKey();
                categoriesKeys.add(key);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String category = dataSnapshot.getValue(String.class);
                String key = dataSnapshot.getKey();

                int index = categoriesKeys.indexOf(key);

                existingCategories.set(index, category);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        databaseProfile.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String categories = dataSnapshot.getValue().toString();
//                Log.d("EventListener", categories);
//                //TODO use spinner instead of  textView to display categories (and allow delete)
//                categories_textView.setText(categories);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        addCategory_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
            }
        });
    }

    private void addCategory() {
        String newCategory = category_editText.getText().toString().trim();

        if(!TextUtils.isEmpty(newCategory)) {
            databaseProfile.push().setValue(newCategory).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CategoriesActivity.this, "Category added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CategoriesActivity.this, "Error saving category", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(this, CreateProfile.class));
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
