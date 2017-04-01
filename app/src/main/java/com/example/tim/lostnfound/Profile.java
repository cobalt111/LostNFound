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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    Intent intentHome = new Intent(Profile.this, MainActivity.class);
//                    finish();
//                    startActivity(intentHome);
//                    return true;
//                case R.id.navigation_listings:
//                    Intent intentListings = new Intent(Profile.this, Listings.class);
//                    finish();
//                    startActivity(intentListings);
//                    return true;
//                case R.id.navigation_animals:
//                    Intent intentAnimals = new Intent(Profile.this, YourPets.class);
//                    finish();
//                    startActivity(intentAnimals);
//                    return true;
////                case R.id.navigation_map:
////                    mTextMessage.setText(R.string.nav_map);
////                    return true;
//            }
//            return false;
//        }
//
//    };


    private FirebaseDatabase mDatabase;
    private DatabaseReference ref;

    private String animalID;
    private HashMap<String, String> animalFound;
    private HashMap<String, String> animal;


    private TextView nameView;
    private TextView colorView;
    private TextView dateView;
    private TextView descView;
    private TextView locationView;
    private TextView phoneView;
    private TextView emailView;
    private TextView statusView;
    private TextView typeView;
    private Button foundButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TEXT)) {
            animalID = intent.getStringExtra(EXTRA_TEXT);
        }

        mDatabase = DatabaseUtils.getDatabase();
        ref = mDatabase.getReference().child("server").child("animals").child(animalID);

        nameView = (TextView) findViewById(R.id.profileName);
        colorView = (TextView) findViewById(R.id.profileColor);
        dateView = (TextView) findViewById(R.id.profileDate);
        descView = (TextView) findViewById(R.id.profileDescription);
        locationView = (TextView) findViewById(R.id.profileLocation);
        phoneView = (TextView) findViewById(R.id.profilePhone);
        emailView = (TextView) findViewById(R.id.profileEmail);
        statusView = (TextView) findViewById(R.id.profileFound);
        typeView = (TextView) findViewById(R.id.profileType);
        foundButton = (Button) findViewById(R.id.profileFoundButton);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                animal = (HashMap<String, String>) dataSnapshot.getValue();
                animalFound = animal;
                String name = animal.get("name");

                if (animal.get("name").equals("")){
                    nameView.setText(animal.get("type"));
                } else {
                    nameView.setText(animal.get("name"));
                    typeView.setText(animal.get("type"));

                }

                colorView.setText(animal.get("color"));
                dateView.setText(animal.get("date"));
                descView.setText(animal.get("description"));
                locationView.setText(animal.get("location"));
                phoneView.setText(animal.get("phone"));
                emailView.setText(animal.get("email"));
                statusView.setText(animal.get("found"));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    Log.d("DEBUG", "Failure");
            }
        });

        foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animalFound.put("found", "Found");
                ref.setValue(animalFound);

                Toast toast = Toast.makeText(getApplicationContext(), "Animal listed as found!", Toast.LENGTH_LONG);
                toast.show();
            }
        });



//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
