package com.example.tim.lostnfound;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tim.lostnfound.FileUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedList;

import static android.content.Intent.EXTRA_TEXT;

public class YourPets extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome = new Intent(YourPets.this, MainActivity.class);
                    finish();
                    startActivity(intentHome);
                    return true;
                case R.id.navigation_listings:
                    Intent intentListings = new Intent(YourPets.this, Listings.class);
                    finish();
                    startActivity(intentListings);
                    return true;
                case R.id.navigation_animals:
                    Intent intentAnimals = new Intent(YourPets.this, YourPets.class);
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


    private ListView listView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference ref;
    private Query query;
    private File file;
    private HashMap<String, String> animal;
    private LinkedList<HashMap<String, String>> animalLinkedList;
    private LinkedList<String> yourAnimalLinkedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_pets);

        mDatabase = DatabaseUtils.getDatabase();
        ref = mDatabase.getReference().child("server").child("animals");

        file = new File(getExternalFilesDir(null).getAbsolutePath(), FileUtils.listOfYourPetsFile);
        animalLinkedList = FileUtils.readFromFile(file);
        yourAnimalLinkedList = new LinkedList<>();

        final Intent ifFoundAnimalIntent = new Intent(this, Profile.class);
        for (final HashMap<String, String> listAnimal : animalLinkedList) {
            query = ref.child(listAnimal.get("key"));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    animal = (HashMap<String, String>) dataSnapshot.getValue();
                    if (!animal.get("found").equals(listAnimal.get("found")) && animal.get("found").equals("Found") && listAnimal.get("notified").equals("false")) {

                        FileUtils.replaceAnimalAsFound(listAnimal, file);

                        Toast toast = Toast.makeText(getApplicationContext(), "One of your lost pets has been found!", Toast.LENGTH_LONG);
                        toast.show();

                        FileUtils.setNotifiedToTrue(listAnimal, file);

                        ifFoundAnimalIntent.putExtra(EXTRA_TEXT, listAnimal.get("key"));
                        finish();
                        startActivity(ifFoundAnimalIntent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        listView = (ListView) findViewById(R.id.listview);

        if (animalLinkedList.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "Your list of animals is empty!", Toast.LENGTH_LONG);
            toast.show();
        } else {
            for (HashMap<String, String> animal : animalLinkedList) {
                yourAnimalLinkedList.add(animal.get("name"));
            }
        }


        ArrayAdapter adapter = new ArrayAdapter(YourPets.this, android.R.layout.simple_list_item_1, yourAnimalLinkedList);
        listView.setAdapter(adapter);

        final LinkedList<HashMap<String, String>> intentList = animalLinkedList;
        final Intent intent = new Intent(this, Profile.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                view.animate().setDuration(100).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                HashMap<String, String> animal = intentList.get(position);
                                intent.putExtra(EXTRA_TEXT, animal.get("key"));
                                startActivity(intent);
                            }
                        });
            }

        });


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


}



//    private void checkIfFound(LinkedList<HashMap<String, String>> animalLinkedList, File file) {
//        for (HashMap<String, String> currentAnimal : animalLinkedList) {
//            if (currentAnimal.get("found") != null && currentAnimal.get("notified") != null){
//                if (currentAnimal.get("found").equals("Found") && currentAnimal.get("notified").equals("false")){
//                    Toast toast = Toast.makeText(getApplicationContext(), "One of your lost pets has been found!", Toast.LENGTH_LONG);
//                    toast.show();
//
//                    currentAnimal.put("notified", "true");
//
//                    Intent foundAnimalIntent = new Intent(YourPets.this, Profile.class);
//                    foundAnimalIntent.putExtra(EXTRA_TEXT, currentAnimal.get("key"));
//                    startActivity(foundAnimalIntent);
//                }
//
//            }
//
//        }
//    }
