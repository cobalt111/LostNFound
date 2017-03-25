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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

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

    final private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

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

    Map<String, String> animal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ref = mDatabase.getReference("server/animals/animals");
        sharedPreferences = this.getSharedPreferences("com.example.tim.lostnfound", Context.MODE_PRIVATE);


        animalID = sharedPreferences.getString("com.example.tim.lostnfound.animal_id", "Failed to retrieve animal ID");
        System.out.println(ref.orderByKey().toString());



        ref.child(animalID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> animal = (Map<String, String>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database retrieval error");
            }
        });

        nameView = (TextView) findViewById(R.id.profileName);
        nameView.setText(animal.get("name"));



        //TextView textView = (TextView) findViewById(R.id.profileTest);
        //textView.setText(sharedPreferences.getString("com.example.tim.lostnfound.animal_id", "Failed"));


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
