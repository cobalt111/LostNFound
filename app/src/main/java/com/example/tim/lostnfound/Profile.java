package com.example.tim.lostnfound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.nav_home);
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

//    final FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private DatabaseReference ref = database.getReference("server/animals");

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        TextView textView = (TextView) findViewById(R.id.profileTest);
        textView.setText(sharedPreferences.getString("animal_id", "Failed"));


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
