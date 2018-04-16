package com.timothycox.lostnfound.activity;


import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.SphericalUtil;
import com.timothycox.lostnfound.R;
import com.timothycox.lostnfound.utilities.Database;

import java.util.HashMap;

public class Map extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Database mDatabase;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng currentUserLatLng;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private int userRadiusPreference;
    private final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private final int REQUEST_CHECK_SETTINGS = 2;


    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }


        LocationSettingsRequest.Builder locationRequestBuilder =
                new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task =
                settingsClient.checkLocationSettings(locationRequestBuilder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                createLocationRequest();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(Map.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) return;

                currentUserLatLng = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());

            }
        };

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mDatabase = Database.getInstance();
        userRadiusPreference = 40234;
        mapFragment.getMapAsync(this);
        setTitle("Map");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
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



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLng, 11));

                            mDatabase.readDataContinuously(new Database.OnGetDataListener() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {

                                    if (currentUserLatLng != null) {
                                        String currentType;
                                        MarkerOptions currentOptions = new MarkerOptions();

                                        for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
                                            @SuppressWarnings("unchecked")
                                            HashMap<String, String> animal = (HashMap<String, String>) animalDatabaseEntry.getValue();

                                            if (animal != null) {
                                                LatLng currentAnimalLatLng = new LatLng(Double.parseDouble(animal.get("latitude")),
                                                        Double.parseDouble(animal.get("longitude")));

                                                if (SphericalUtil.computeDistanceBetween(currentAnimalLatLng, currentUserLatLng) < userRadiusPreference) {
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

                                                    currentOptions.snippet(animal.get("key"));
                                                    currentOptions.position(currentAnimalLatLng);
                                                    if (animal.get("name").equals("")) {
                                                        currentOptions.title(animal.get("type"));
                                                    } else currentOptions.title(animal.get("name"));

                                                    mMap.addMarker(currentOptions);
                                                }

                                            } else Log.d("MapLoop", "Animal was null");
                                        }
                                    }
                                }

                                @Override
                                public void onStart() {
                                }

                                @Override
                                public void onFailure(DatabaseError databaseError) {
                                }
                            });

                        }
                    }
                });


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
