package com.example.tim.lostnfound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.tim.lostnfound.DatabaseUtils;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static android.content.Intent.EXTRA_TEXT;

public class Profile extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome = new Intent(Profile.this, MainActivity.class);
                    finish();
                    startActivity(intentHome);
                    return true;
                case R.id.navigation_listings:
                    Intent intentListings = new Intent(Profile.this, Listings.class);
                    finish();
                    startActivity(intentListings);
                    return true;
                case R.id.navigation_animals:
                    Intent intentAnimals = new Intent(Profile.this, YourPets.class);
                    finish();
                    startActivity(intentAnimals);
                    return true;
//                case R.id.navigation_map:
//                    mTextMessage.setText(R.string.nav_map);
//                    return true;
            }
            return false;
        }

    };


    FirebaseDatabase mDatabase;
    DatabaseReference ref;
    SharedPreferences sharedPreferences;

    String animalID;

    TextView nameView;
    TextView colorView;
    TextView dateView;
    TextView descView;
    TextView locationView;
    TextView phoneView;
    TextView emailView;
    TextView typeView;

    HashMap<String, String> animal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        animalID = intent.getStringExtra(EXTRA_TEXT);

//        sharedPreferences = this.getSharedPreferences("com.example.tim.lostnfound", Context.MODE_PRIVATE);
//        animalID = sharedPreferences.getString("com.example.tim.lostnfound." + lastUsedKey, "Failed to retrieve animal ID");

        mDatabase = DatabaseUtils.getDatabase();
        ref = mDatabase.getReference().child("server").child("animals").child(animalID);

        nameView = (TextView) findViewById(R.id.profileName);
        colorView = (TextView) findViewById(R.id.profileColor);
        dateView = (TextView) findViewById(R.id.profileDate);
        descView = (TextView) findViewById(R.id.profileDescription);
        locationView = (TextView) findViewById(R.id.profileLocation);
        phoneView = (TextView) findViewById(R.id.profilePhone);
        emailView = (TextView) findViewById(R.id.profileEmail);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                animal = (HashMap<String, String>) dataSnapshot.getValue();

                nameView.setText(animal.get("name"));
                colorView.setText(animal.get("color"));
                dateView.setText(animal.get("date"));
                descView.setText(animal.get("description"));
                locationView.setText(animal.get("location"));
                phoneView.setText(animal.get("phone"));
                emailView.setText(animal.get("email"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    Log.d("DEBUG", "Failure");
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
