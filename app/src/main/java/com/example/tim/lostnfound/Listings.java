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

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.EXTRA_INDEX;

public class Listings extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.nav_home);
                    Intent intentHome = new Intent(Listings.this, MainActivity.class);
                    startActivity(intentHome);
                    return true;
                case R.id.navigation_listings:
                    Intent intentListings = new Intent(Listings.this, Listings.class);
                    startActivity(intentListings);
                    return true;
                case R.id.navigation_animals:
                    Intent intentAnimals = new Intent(Listings.this, MainActivity.class);
                    startActivity(intentAnimals);
                    return true;
//                case R.id.navigation_map:
//                    mTextMessage.setText(R.string.nav_map);
//                    return true;
            }
            return false;
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);


        final ListView listView = (ListView) findViewById(R.id.listview);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i <values.length; i++){
            list.add(values[i]);
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);

        final Intent intent = new Intent(this, Profile.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //TODO figure out how to pass the ID to the other activity
//                                int ID =
//                                intent.putExtra(EXTRA_INDEX, ID);
                                startActivity(intent);


//                                list.remove(item);
//                                adapter.notifyDataSetChanged();
//                                view.setAlpha(1);
                            }
                        });
            }

        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
}
