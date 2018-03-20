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
import android.widget.ImageButton;
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
    private HashMap<String, Object> animalFound;
    private HashMap<String, Object> animal;

    // Declare UI elements
    private TextView nameView;
    private TextView colorView;
    private TextView dateView;
    private TextView descView;
    private TextView locationView;
    private TextView phoneView;
    private TextView emailView;
    private TextView statusView;
    private TextView typeView;
    private ImageButton changeStatusButton;

    // new buttons for capstone
    private ImageButton removeListingButton;
    private ImageButton editListingButton;

    // TODO discuss best implementation of "match to other pets" with group
    private ImageButton matchToLostButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retrieve animalID from the intent used to start this instance of Profile
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TEXT)) {
            animalID = intent.getStringExtra(EXTRA_TEXT);
        }

        // Initialize UI elements
        nameView = (TextView) findViewById(R.id.profileName);
        colorView = (TextView) findViewById(R.id.profileColor);
        dateView = (TextView) findViewById(R.id.profileDate);
        descView = (TextView) findViewById(R.id.profileDescription);
        locationView = (TextView) findViewById(R.id.profileLocation);
        phoneView = (TextView) findViewById(R.id.profilePhone);
        emailView = (TextView) findViewById(R.id.profileEmail);
        statusView = (TextView) findViewById(R.id.profileFound);
        typeView = (TextView) findViewById(R.id.profileType);


        // Initialize button and listener
        changeStatusButton = (ImageButton) findViewById(R.id.profileChangeButton);
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animalFound.put("found", "Found");
                ref.setValue(animalFound);

                Toast toast = Toast.makeText(getApplicationContext(), "Animal listed as found!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        // Initialize button and listener
        editListingButton = (ImageButton) findViewById(R.id.profileEditButton);
        editListingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Post.class);
                intent.putExtra("animalID", animalID);
                finish();
                startActivity(intent);

            }
        });


        // Create reference to database
        mDatabase = DatabaseUtils.getDatabase();

        // Find the particular animal in the database according to the animalID passed in the intent
        ref = mDatabase.getReference().child("server").child("animals").child(animalID);

        // Contact database, retrieve data elements and display them in the appropriate view
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                animal = (HashMap<String, Object>) dataSnapshot.getValue();
                animalFound = animal;

                if (animal.get("name").equals("")){
                    nameView.setText("(none listed)");
                } else {
                    nameView.setText(animal.get("name").toString());
                }

                if (animal.get("color").equals("")){
                    colorView.setText("(none listed)");
                } else {
                    colorView.setText(animal.get("color").toString());
                }

                if (animal.get("date").equals("")){
                    dateView.setText("(none listed)");
                } else {
                    dateView.setText(animal.get("date").toString());
                }

                if (animal.get("description").equals("")){
                    descView.setText("(none listed)");
                } else {
                    descView.setText(animal.get("description").toString());
                }

                if (animal.get("location").equals("")){
                    locationView.setText("(none listed)");
                } else {
                    locationView.setText(animal.get("location").toString());
                }

                if (animal.get("phone").equals("")){
                    phoneView.setText("(none listed)");
                } else {
                    phoneView.setText(animal.get("phone").toString());
                }

                if (animal.get("email").equals("")){
                    emailView.setText("(none listed)");
                } else {
                    emailView.setText(animal.get("email").toString());
                }

                typeView.setText(animal.get("type").toString());

                statusView.setText(animal.get("found").toString());


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                    Log.d("DEBUG", "Failure");
            }
        });



    }
}
