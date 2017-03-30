package com.example.tim.lostnfound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.*;
import java.util.Map;

import static android.content.Intent.EXTRA_INDEX;
import static android.content.Intent.EXTRA_TEXT;

public class Listings extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome = new Intent(Listings.this, MainActivity.class);
                    finish();
                    startActivity(intentHome);
                    return true;
                case R.id.navigation_listings:
                    Intent intentListings = new Intent(Listings.this, Listings.class);
                    finish();
                    startActivity(intentListings);
                    return true;
                case R.id.navigation_animals:
                    Intent intentAnimals = new Intent(Listings.this, YourPets.class);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.listings_options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.lost_animals:
                String lostFilter = "Lost";
                Intent lostFilterIntent = new Intent(this, Listings.class);
                lostFilterIntent.putExtra("filter", lostFilter);
                finish();
                startActivity(lostFilterIntent);
                return true;
            case R.id.found_animals:
                String foundFilter = "Found";
                Intent foundFilterIntent = new Intent(this, Listings.class);
                foundFilterIntent.putExtra("filter", foundFilter);
                finish();
                startActivity(foundFilterIntent);
                return true;
            case R.id.all_animals:
                String noFilter = null;
                Intent noFilterIntent = new Intent(this, Listings.class);
                noFilterIntent.putExtra("filter", noFilter);
                finish();
                startActivity(noFilterIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private FirebaseDatabase mDatabase;
    private DatabaseReference ref;
    private HashMap<String, String> animal;
    private ArrayList<HashMap<String, String>> animalArrayList;
    private ArrayList<String> nameArrayList;
    private ListView listView;
    private String userIntention;
    private Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        Intent intent = getIntent();
        if (intent.hasExtra("filter")) {
            userIntention = intent.getStringExtra("filter");
        }


        mDatabase = DatabaseUtils.getDatabase();
        ref = mDatabase.getReference().child("server").child("animals");
        query = ref;

        // just using namearraylist for the time being, will probably change to two line adapter
        listView = (ListView) findViewById(R.id.listview);
        animalArrayList = new ArrayList<>();
        nameArrayList = new ArrayList<>();


        if (userIntention != null) {
            query = ref.orderByChild("found").equalTo(userIntention);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
                    animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
                    animalArrayList.add(animal);

                    if (animal.get("name").equals("")) {
                        nameArrayList.add(animal.get("type"));
                    } else nameArrayList.add(animal.get("name"));

                }


                ArrayAdapter adapter = new ArrayAdapter(Listings.this, android.R.layout.simple_list_item_1, nameArrayList);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Intent listIntent = new Intent(this, Profile.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {

                view.animate().setDuration(0).alpha(1)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                animal = animalArrayList.get(position);
                                listIntent.putExtra(EXTRA_TEXT, animal.get("key"));
                                startActivity(listIntent);

                            }
                        });
            }

        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
}


//                switch (userIntention) {
//                        case ("lost"):
//                        for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
//                        animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
//
//        if (animal.get("found").equals("lost")) {
//        animalArrayList.add(animal);
//
//        if (animal.get("name").equals("")) {
//        nameArrayList.add(animal.get("type"));
//        } else nameArrayList.add(animal.get("name"));
//        }
//        }
//        case ("found"):
//        for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
//        animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
//
//        if (animal.get("found").equals("found")) {
//        animalArrayList.add(animal);
//
//        if (animal.get("name").equals("")) {
//        nameArrayList.add(animal.get("type"));
//        } else nameArrayList.add(animal.get("name"));
//        }
//        }
//default:
//        for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
//        animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
//        animalArrayList.add(animal);
//
//
//        if (animal.get("name").equals("")) {
//        nameArrayList.add(animal.get("type"));
//        } else nameArrayList.add(animal.get("name"));
//        }
//        }




//        }
//        else {
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
//                        animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
//                        animalArrayList.add(animal);
//
//                        if (animal.get("name").equals("")) {
//                            nameArrayList.add(animal.get("type"));
//                        } else nameArrayList.add(animal.get("name"));
//                    }
//
//
//                    ArrayAdapter adapter = new ArrayAdapter(Listings.this, android.R.layout.simple_list_item_1, nameArrayList);
//                    listView.setAdapter(adapter);
//
//                }
//
//
//
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("DEBUG", "Failure");
//                }
//            });
//        }