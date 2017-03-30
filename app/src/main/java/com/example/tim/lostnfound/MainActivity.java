package com.example.tim.lostnfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome = new Intent(MainActivity.this, MainActivity.class);
                    finish();
                    startActivity(intentHome);
                    return true;
                case R.id.navigation_listings:
                    Intent intentListings = new Intent(MainActivity.this, Listings.class);
                    finish();
                    startActivity(intentListings);
                    return true;
                case R.id.navigation_animals:
                    Intent intentAnimals = new Intent(MainActivity.this, YourPets.class);
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





    private FirebaseDatabase mDatabase;
    private DatabaseReference ref;
    private Query query;
    private HashMap<String, String> animal;
    private Button foundButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foundButton = (Button) findViewById(R.id.mainFoundButton);
        foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Post.class);
                finish();
                startActivity(intent);
            }
        });

        File file = new File(getExternalFilesDir(null).getAbsolutePath(), FileUtils.listOfYourPetsFile);
        LinkedList<HashMap<String, String>> animalLinkedList = FileUtils.readFromFile(file);

        mDatabase = DatabaseUtils.getDatabase();
        ref = mDatabase.getReference().child("server").child("animals");

        for (final HashMap<String, String> listAnimal : animalLinkedList) {
            query = ref.child(listAnimal.get("key"));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
                        animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
                        if (!animal.get("found").equals(listAnimal.get("found")) && animal.get("found").equals("Found")) {
                            listAnimal.put("found", animal.get("found"));
                            listAnimal.put("notified", "false");
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        FileUtils.writeToFile(animalLinkedList, file);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
