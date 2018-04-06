package com.example.tim.lostnfound;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.widget.Toast.LENGTH_LONG;
import static com.example.tim.lostnfound.LocationAddress.getAddressFromLocation;


public class Search extends AppCompatActivity implements LocationListener {

    // Reference to database
    private DatabaseReference dataReference;



    // Declaring local variables
    private List<String> yourAnimalList;


    // Declaring UI elements
    private Spinner typeDropdown;
    private Spinner statusDropdown;
    private String typeSelection;
    private String statusSelection;
    private ImageButton searchButton;
    private EditText nameView;
    private EditText colorView;
    private EditText dateView;
    private EditText descView;
    private EditText locationView;
    private EditText phoneView;
    private EditText emailView;



    protected LocationManager locationManager;
    protected Location location;


    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStackImmediate();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // TODO handle location based errors
        // finds current location (lat long coordinates) to place the pin on the map
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);
        location = getLastKnownLocation();
        LocationAddress locationAddress = new LocationAddress();
        getAddressFromLocation( location.getLatitude(),
                                location.getLongitude(),
                                getApplicationContext(),
                                new GeocoderHandler());


        // Create reference to database
        dataReference = DatabaseUtils.getReference(DatabaseUtils.getDatabase());

        // Initialize UI elements
        nameView = (EditText) findViewById(R.id.postName);
        colorView = (EditText) findViewById(R.id.postColor);
        dateView = (EditText) findViewById(R.id.postDate);
        emailView = (EditText) findViewById(R.id.postEmail);
        descView = (EditText) findViewById(R.id.postDescription);
        phoneView = (EditText) findViewById(R.id.postPhone);
        locationView = (EditText) findViewById(R.id.postLocation);


        // Initialize dropdown selection spinner for animal type
        typeDropdown = (Spinner) findViewById(R.id.post_type_spinner);
        final String[] typesList = {"Dog",
                                    "Cat",
                                    "Hamster/Guinea Pig",
                                    "Mouse/Rat",
                                    "Bird",
                                    "Snake/Lizard",
                                    "Ferret",
                                    "Other"
        };
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(Search.this, android.R.layout.simple_spinner_item, typesList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeDropdown.setAdapter(typeAdapter);

        typeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSelection = typesList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                typeSelection = null;
            }
        });

        // Same as above, Initializing dropdown selection spinner for status
        statusDropdown = (Spinner) findViewById(R.id.post_status_spinner);
        final String[] statusList = {"Lost", "Found"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(Search.this, android.R.layout.simple_spinner_item, statusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusDropdown.setAdapter(statusAdapter);

        statusDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusSelection = statusList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                statusSelection = null;
            }
        });

        // button to add picture?
        // TODO add picture functionality


        // Initialize submit button and listener
        searchButton = (ImageButton) findViewById(R.id.searchSubmit);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){




            }
        });

    }


    // Implementing method to submit animal to database and return its unique key
//    private Intent onSubmit() {
//
//        // Declare HashMap to enter animal data into, and declare reference to database to add the HashMap to
//        HashMap<String, Object> animal = new HashMap<>();
//
//        // Collect entered data and add it to the HashMap
//        animal.put("name", nameView.getText().toString());
//        animal.put("color", colorView.getText().toString());
//        animal.put("date", dateView.getText().toString());
//        animal.put("email", emailView.getText().toString());
//        animal.put("description", descView.getText().toString());
//        animal.put("phone", phoneView.getText().toString());
//        animal.put("location", locationView.getText().toString());
//        animal.put("latitude", Double.toString(location.getLatitude()));
//        animal.put("longitude", Double.toString(location.getLongitude()));
//        animal.put("type", typeSelection);
//        animal.put("found", statusSelection);
//
//        //TODO add picture functionality
//
//
//        Intent searchResultsIntent = new Intent(getApplicationContext(), ListingsFragment.class);
//
//        return ;
//    }



    private static class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Log.d("location", locationAddress);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


//    // for taking the pictures
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//
//    // for saving the image with a new filename
//    String mCurrentPhotoPath;
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

//    // to display the image
//    private void setPic() {
//        // Get the dimensions of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        mImageView.setImageBitmap(bitmap);
//    }




}