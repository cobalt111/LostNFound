package com.example.tim.lostnfound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


    FirebaseDatabase mDatabase;
    DatabaseReference ref;
//    SharedPreferences sharedPreferences;
    HashMap<String, String> animal;
    ArrayList<HashMap<String, String>> animalArrayList;
    ArrayList<String> nameArrayList;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        mDatabase = DatabaseUtils.getDatabase();
        ref = mDatabase.getReference().child("server").child("animals");

        // just using namearraylist for the time being
        listView = (ListView) findViewById(R.id.listview);
        animalArrayList = new ArrayList<>();
        nameArrayList = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()){
                    animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
                    animalArrayList.add(animal);
                    nameArrayList.add(animal.get("name"));
                }

                ArrayAdapter adapter = new ArrayAdapter(Listings.this, android.R.layout.simple_list_item_1, nameArrayList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DEBUG", "Failure");
            }
        });





        final Intent intent = new Intent(this, Profile.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
//                final String item = (String) parent.getItemAtPosition(position);

                view.animate().setDuration(100).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //TODO pass the ID to the other activity
                                animal = animalArrayList.get(position);
                                intent.putExtra(EXTRA_TEXT, animal.get("key"));
                                finish();
                                startActivity(intent);

                            }
                        });
            }

        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
}
