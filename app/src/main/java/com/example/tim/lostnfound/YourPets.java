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

import java.io.File;
import java.util.ArrayList;
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


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_pets);

        File file = new File(getExternalFilesDir(null).getAbsolutePath(), "animal_key_list.txt");

        LinkedList<HashMap<String, String>> animalLinkedList = FileUtils.readFromFile(file);
        final LinkedList<HashMap<String, String>> intentList = animalLinkedList;
        ArrayList<String> yourAnimalArrayList = new ArrayList<>();


        listView = (ListView) findViewById(R.id.listview);

        if (animalLinkedList.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "Your list of lost animals is empty!", Toast.LENGTH_LONG);
            toast.show();
        } else {
            for (HashMap<String, String> animal : animalLinkedList) {
                yourAnimalArrayList.add(animal.get("name"));
            }
        }



        ArrayAdapter adapter = new ArrayAdapter(YourPets.this, android.R.layout.simple_list_item_1, yourAnimalArrayList);
        listView.setAdapter(adapter);

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
