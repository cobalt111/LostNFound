//package com.example.tim.lostnfound;
//
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.design.widget.BottomNavigationView;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MenuItem;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.HashMap;
//
//public class Filter extends AppCompatActivity {
//
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    Intent intentHome = new Intent(Filter.this, MainActivity.class);
//                    finish();
//                    startActivity(intentHome);
//                    return true;
//                case R.id.navigation_listings:
//                    Intent intentListings = new Intent(Filter.this, Listings.class);
//                    finish();
//                    startActivity(intentListings);
//                    return true;
//                case R.id.navigation_animals:
//                    Intent intentAnimals = new Intent(Filter.this, YourPets.class);
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
//
//
//    private FirebaseDatabase mDatabase;
//    private DatabaseReference ref;
//
//    private HashMap<String, String> animal;
//
//
//    private String name;
//    private String type;
//    private String color;
//    private String date;
//    private String found;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_filter);
//
//        Intent intent = getIntent();
////        animalID = intent.getStringExtra(???);
//
//        mDatabase = DatabaseUtils.getDatabase();
//        ref = mDatabase.getReference().child("server").child("animals");
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                animal = (HashMap<String, String>) dataSnapshot.getValue();
//
//                nameView.setText(animal.get("name"));
//                colorView.setText(animal.get("color"));
//                dateView.setText(animal.get("date"));
//                descView.setText(animal.get("description"));
//                locationView.setText(animal.get("location"));
//                phoneView.setText(animal.get("phone"));
//                emailView.setText(animal.get("email"));
//                foundView.setText(animal.get("found"));
//                typeView.setText(animal.get("type"));
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("DEBUG", "Data Retrieval Failure");
//            }
//        });
//
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
//
//
//    }
//}
