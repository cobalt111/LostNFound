package com.example.tim.lostnfound;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String addingMarker;

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

        LatLng flint = new LatLng(43.012527, -83.687456);
        mMap.addMarker(new MarkerOptions().position(flint).title("Welcome to Flint"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(flint, 13));


        Intent intent = getIntent();
        if (intent.hasExtra("adding")) {
            addingMarker = intent.getStringExtra("adding");
        }


        if (addingMarker.equals("adding")) {
            // Setting a click event handler for the map
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {

                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting the position for the marker
                    markerOptions.position(latLng);

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
//                    markerOptions.title();

                    // Clears the previously touched position
                    mMap.clear();

                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Placing a marker on the touched position
                    mMap.addMarker(markerOptions);


                    Intent startProfileIntent = new Intent(MapsActivity.this, Profile.class);

                    startProfileIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("latlng", latLng);

                    startProfileIntent.putExtra("latlng", bundle);


                    finish();
                    startActivity(startProfileIntent);

                }
            });
        } else {

        }
    }
}
