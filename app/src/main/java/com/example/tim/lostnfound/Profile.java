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


import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Profile extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome = new Intent(Profile.this, MainActivity.class);
                    startActivity(intentHome);
                    return true;
                case R.id.navigation_listings:
                    Intent intentListings = new Intent(Profile.this, Listings.class);
                    startActivity(intentListings);
                    return true;
                case R.id.navigation_animals:
                    Intent intentAnimals = new Intent(Profile.this, MainActivity.class);
                    startActivity(intentAnimals);
                    return true;
//                case R.id.navigation_map:
//                    mTextMessage.setText(R.string.nav_map);
//                    return true;
            }
            return false;
        }

    };


    SharedPreferences sharedPreferences;

    String animalID;
    String name;
    String location;


    TextView nameView;
    TextView colorView;
    TextView dateView;
    TextView descView;
    TextView locationView;
    TextView phoneView;
    TextView emailView;
    TextView typeView;

    NewAnimal animal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = this.getSharedPreferences("com.example.tim.lostnfound", Context.MODE_PRIVATE);
        animalID = sharedPreferences.getString("com.example.tim.lostnfound.animal_id", "Failed to retrieve animal ID");

        final FirebaseDatabase mDatabase = DatabaseUtils.getDatabase();
        DatabaseReference ref = mDatabase.getReference().child("server").child("animals").child(animalID);


//        final CountDownLatch latch = new CountDownLatch(2);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    animal = (NewAnimal) dataSnapshot.getValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    Log.d("DEBUG", "Failure");
            }
        });

        nameView = (TextView) findViewById(R.id.profileName);
        nameView.setText(animal.getName());



        //TextView textView = (TextView) findViewById(R.id.profileTest);
        //textView.setText(sharedPreferences.getString("com.example.tim.lostnfound.animal_id", "Failed"));


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
