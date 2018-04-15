package com.example.tim.lostnfound.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.tim.lostnfound.R;
import com.example.tim.lostnfound.utilities.Database;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.List;

public class Map extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Database mDatabase;
    private HashMap<String, String> animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        setTitle("Map");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        LatLng flint = new LatLng(43.012527, -83.687456);

        mDatabase = Database.getInstance();
        mDatabase.readDataContinuously(new Database.OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String currentType;
                MarkerOptions currentOptions = new MarkerOptions();

                for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
                    animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
//                    animalList.add(animal);

                    currentType = animal.get("type");
                    switch (currentType) {
                        case "Dog":
                            if (animal.get("found").equals("Found")) {
                                currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_pin_small));
                            } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_pin_small));
                            break;
                        case "Cat":
                            if (animal.get("found").equals("Found")) {
                                currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_cat));
                            } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_cat));
                            break;
                        case "Bird":
                            if (animal.get("found").equals("Found")) {
                                currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_bird));
                            } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_bird));
                            break;
                        case "Ferret":
                            if (animal.get("found").equals("Found")) {
                                currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_ferret));
                            } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_ferret));
                            break;
                        case "Hamster/Guinea Pig":
                            if (animal.get("found").equals("Found")) {
                                currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_hamster));
                            } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_hamster));
                            break;
                        case "Mouse/Rat":
                            if (animal.get("found").equals("Found")) {
                                currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_mouse));
                            } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_mouse));
                            break;
                        case "Snake/Lizard":
                            if (animal.get("found").equals("Found")) {
                                currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_snake));
                            } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_snake));
                            break;
                        case "Other":
                            if (animal.get("found").equals("Found")) {
                                currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_other));
                            } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_other));
                            break;
                        default:
                            if (animal.get("found").equals("Found")) {
                                currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_other));
                            } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_other));
                            break;
                    }

                    LatLng currentLatLng = new LatLng(  Double.parseDouble(animal.get("latitude")),
                                                        Double.parseDouble(animal.get("longitude")));

                    currentOptions.snippet(animal.get("key"));
                    currentOptions.position(currentLatLng);
                    if (animal.get("name").equals("")) {
                        currentOptions.title(animal.get("type"));
                    } else currentOptions.title(animal.get("name"));

                    mMap.addMarker(currentOptions);

                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(DatabaseError databaseError) {

            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(flint, 11));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Intent profileIntent = new Intent(Map.this, Profile.class);
        String animalID = marker.getSnippet();
        profileIntent.putExtra("animalID", animalID);

        startActivity(profileIntent);

        return true;
    }
}
