package com.example.tim.lostnfound;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String addingMarker;
    private FirebaseDatabase database;
    private DatabaseReference dataReference;
    private LinkedList<HashMap<String, String>> animalLinkedList;
    private HashMap<String, String> animal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);




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


        Log.d("map", "map is ready");
        mMap.setOnMarkerClickListener(this);
        LatLng flint = new LatLng(43.012527, -83.687456);

        database = DatabaseUtils.getDatabase();
        dataReference = DatabaseUtils.getReference(database);

        animalLinkedList = new LinkedList<>();

        dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot animalDatabase : dataSnapshot.getChildren()) {
                    animal = (HashMap<String, String>) animalDatabase.getValue();
                    animalLinkedList.add(animal);

                }

                for (HashMap<String, String> currentAnimal : animalLinkedList) {

                    MarkerOptions currentOptions = new MarkerOptions();
                    LatLng currentLatLng = new LatLng(Double.parseDouble(currentAnimal.get("latitude")),
                            Double.parseDouble(currentAnimal.get("longitude")));

                    currentOptions.position(currentLatLng);
                    if (currentAnimal.get("name").equals("")) {
                        currentOptions.title(currentAnimal.get("type"));
                    } else currentOptions.title(currentAnimal.get("name"));

                    currentOptions.snippet(currentAnimal.get("key"));
                    if (currentAnimal.get("found").equals("Found")) {
                        currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found_pin_small));

                    } else currentOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost_pin_small));


                    mMap.addMarker(currentOptions);


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(flint, 11));



    }





//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
//                PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
//                        PackageManager.PERMISSION_GRANTED) {
//            googleMap.setMyLocationEnabled(true);
//            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//        } else {
//            ActivityCompat.requestPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, TAG_CODE_PERMISSION_LOCATION);
//        }

        //get current location

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//
//            }
//        });



    @Override
    public boolean onMarkerClick(Marker marker) {

        Intent profileIntent = new Intent(MapsActivity.this, Profile.class);
        String animalID = marker.getSnippet();
        profileIntent.putExtra(Intent.EXTRA_TEXT, animalID);

        startActivity(profileIntent);

        return true;
    }
}
