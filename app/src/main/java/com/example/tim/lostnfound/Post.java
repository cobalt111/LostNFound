package com.example.tim.lostnfound;



import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tim.lostnfound.LocationAddress;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static android.content.Intent.EXTRA_TEXT;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.widget.Toast.LENGTH_LONG;
import static com.example.tim.lostnfound.LocationAddress.getAddressFromLocation;


public class Post extends AppCompatActivity implements LocationListener {

    // Reference to database
    private DatabaseReference dataReference;

    // The FirebaseStorage object is used to upload the pictures. StorageReference is the reference to the particular file uploaded
    private FirebaseStorage storage;
    private StorageReference storageReference;

    // Declaring local variables
    private List<String> yourAnimalList;
    private String editAnimalID;
    private boolean isEditInstance;
    private boolean isImagePicked;
    private Bitmap imageBmp;
    private Uri imageUri;

    // Declaring UI elements
    private Spinner typeDropdown;
    private Spinner statusDropdown;
    private ImageButton picButton;
    private String typeSelection;
    private String statusSelection;
    private ImageButton submitButton;
    private ImageButton selectImageButton;
    private EditText nameView;
    private EditText colorView;
    private EditText dateView;
    private EditText descView;
    private EditText locationView;
    private EditText phoneView;
    private EditText emailView;

    //  REQUEST_IMAGE_CAPTURE is a request code to switch to the activity for picking images from gallery/camera
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_REQUEST = 2;


    protected LocationManager locationManager;
    protected Location location;

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    try {
                        locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    location = getLastKnownLocation();
                    LocationAddress locationAddress = new LocationAddress();
                    getAddressFromLocation(location.getLatitude(), location.getLongitude(),
                            getApplicationContext(), new GeocoderHandler());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // TODO handle location based errors
        // finds current location (lat long coordinates) to place the pin on the map
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        } else {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);
            location = getLastKnownLocation();
            LocationAddress locationAddress = new LocationAddress();
            getAddressFromLocation(location.getLatitude(), location.getLongitude(),
                    getApplicationContext(), new GeocoderHandler());
        }




        // Verify local data file exists, and create it if not verified
        FileUtils.createFile(getApplicationContext());

        // Import animal data from local file
        yourAnimalList = FileUtils.readFromFile(getApplicationContext());

        // Create reference to database
        dataReference = DatabaseUtils.getReference(DatabaseUtils.getDatabase());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialize UI elements
        nameView = (EditText) findViewById(R.id.postName);
        colorView = (EditText) findViewById(R.id.postColor);
        dateView = (EditText) findViewById(R.id.postDate);
        emailView = (EditText) findViewById(R.id.postEmail);
        descView = (EditText) findViewById(R.id.postDescription);
        phoneView = (EditText) findViewById(R.id.postPhone);
        locationView = (EditText) findViewById(R.id.postLocation);

        isImagePicked = false;


        // button to open camera and take picture
        picButton = (ImageButton) findViewById(R.id.postImage);
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Post.this);
                builder.setTitle("Add Photo");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Take Photo")) {
                            isImagePicked = true;
                            dispatchTakePictureIntent();
                        } else if (items[item].equals("Choose from Library")) {
                            isImagePicked = true;
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, GALLERY_REQUEST);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

            }
        });

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
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(Post.this, android.R.layout.simple_spinner_item, typesList);
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
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(Post.this, android.R.layout.simple_spinner_item, statusList);
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



        // Retrieve animalID from the intent used to start this instance of Profile
        Intent intent = getIntent();

        // If the intent has a passed animalID, this must be an instance of editing
        // an existing post rather than normal posting
        // We want to change the existing animal with the entered data rather than create an entirely
        // new listing for the same animal with slightly edited data elements
        // Because we are editing existing data, the current data is retrieved and is automatically
        // filled in. The user should edit whatever data elements are incorrect and hit submit.
        if (intent.hasExtra("editInstance") && intent.getStringExtra("editInstance").equals("true")) {
            isEditInstance = true;
            editAnimalID = intent.getStringExtra("animalID");

            // Find the particular animal in the database according to the animalID passed in the intent
            dataReference = dataReference.child(editAnimalID);

            // Contact database, retrieve data elements and display them in the appropriate view
            dataReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    @SuppressWarnings("unchecked")
                    HashMap<String, String> animal = (HashMap<String, String>) dataSnapshot.getValue();

                    nameView.setText(animal.get("name"));
                    colorView.setText(animal.get("color"));
                    dateView.setText(animal.get("date"));
                    emailView.setText(animal.get("email"));
                    descView.setText(animal.get("description"));
                    phoneView.setText(animal.get("phone"));
                    locationView.setText(animal.get("location"));

                    typeSelection = animal.get("type");
                    for (int i = 0; i < typesList.length; i++) {
                        if (typeSelection.equals(typesList[i])) {
                            typeDropdown.setSelection(i);
                            break;
                        }
                    }

                    statusSelection = animal.get("found");
                    for (int i = 0; i < statusList.length; i++) {
                        if (typeSelection.equals(statusList[i])) {
                            typeDropdown.setSelection(i);
                            break;
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("DEBUG", "Failure");
                }
            });

            // TODO retrieve already uploaded picture for editing
        }


        // Initialize submit button and listener
        submitButton = (ImageButton) findViewById(R.id.postSubmit);
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String animalID;
                Intent intent = new Intent(Post.this, Profile.class);


                // find out whether to run the onEdit method or the onSubmit method
                if (isEditInstance) {

                    // Once the submit button is clicked, the onEdit method will do the work of editing the info in the database.
                    // animalID is the database key for animal.
                    animalID = onEdit(editAnimalID);
                    intent.putExtra("fromEditInstance", "true");

                    // TODO add code for submission verification before displaying success toast
                    Toast toast = Toast.makeText(getApplicationContext(), "Your pet listing has been edited!", LENGTH_LONG);
                    toast.show();

                } else {
                    // Once the submit button is clicked, the onSubmitAnimal method will do the work of submitting the info to the database.
                    // animalID is the database key for animal.
                    animalID = onSubmit();

                    // TODO add code for submission verification before displaying success toast
                    Toast toast = Toast.makeText(getApplicationContext(), "Your pet has been posted!", LENGTH_LONG);
                    toast.show();

                }


                // Create
                // to open profile of submitted/edited animal
                intent.putExtra("animalID", animalID);
                finish();
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            imageBmp = (Bitmap) extras.get("data");

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File image = File.createTempFile(

                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                picButton.setImageURI(Uri.parse(image.getAbsolutePath()));


            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO figure out how to represent picked image on post activity
//            mImageView.setImageBitmap(imageBitmap);

        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            imageUri = data.getData();
            picButton.setImageURI(imageUri);

        }
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
        }
    }



    private String onEdit(final String editAnimalID) {


        yourAnimalList = FileUtils.readFromFile(getApplicationContext());

        // Find the old animal in yourAnimalList and delete it
        if (yourAnimalList.size() > 0) {
            String animalID;
            int length = yourAnimalList.size();
            for (int i = 0; i < length; i++) {
                animalID = yourAnimalList.get(i);
                if (animalID.equals(editAnimalID)) {
                    yourAnimalList.remove(animalID);
                    break;
                }
            }
        }


        // Declare HashMap to enter animal data into, and declare reference to database to update
        HashMap<String, Object> animal = new HashMap<>();

        // Collect entered data and add it to the HashMap
        animal.put("name", nameView.getText().toString());
        animal.put("color", colorView.getText().toString());
        animal.put("date", dateView.getText().toString());
        animal.put("email", emailView.getText().toString());
        animal.put("description", descView.getText().toString());
        animal.put("phone", phoneView.getText().toString());
        animal.put("location", locationView.getText().toString());
        animal.put("latitude", Double.toString(location.getLatitude()));
        animal.put("longitude", Double.toString(location.getLongitude()));
        animal.put("type", typeSelection);
        animal.put("found", statusSelection);
        animal.put("key", editAnimalID);




        //TODO get photo editing working
        if (isImagePicked) {

            if (imageBmp != null) {
                StorageReference animalStorageRef = storageReference.child("server")
                        .child("animals")
                        .child("images")
                        .child("thumb/" + editAnimalID);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = animalStorageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // todo Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri imageURL = taskSnapshot.getDownloadUrl();

                        dataReference.child(editAnimalID).child("thumbURL").setValue(imageURL.toString());

                    }
                });

            } else if (imageUri != null) {

                StorageReference animalStorageRef = storageReference.child("server")
                        .child("animals")
                        .child("images")
                        .child("thumb/" + editAnimalID);

                UploadTask uploadTask = animalStorageRef.putFile(imageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // todo Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri imageURL = taskSnapshot.getDownloadUrl();

                        dataReference.child(editAnimalID).child("thumbURL").setValue(imageURL.toString());

                    }
                });

            } else Log.d("Image", "Unable to submit image reference");


        }

        // Update database with edited information
        dataReference.updateChildren(animal);

        // Add animal to local list of animals and save it to the data file
        yourAnimalList.add(editAnimalID);
        FileUtils.writeToFile(yourAnimalList, getApplicationContext());

        return editAnimalID;
    }



    // Implementing method to submit animal to database and return its unique key
    private String onSubmit() {

        // Declare HashMap to enter animal data into, and declare reference to database to add the HashMap to
        HashMap<String, Object> animal = new HashMap<>();

        // Collect entered data and add it to the HashMap
        animal.put("name", nameView.getText().toString());
        animal.put("color", colorView.getText().toString());
        animal.put("date", dateView.getText().toString());
        animal.put("email", emailView.getText().toString());
        animal.put("description", descView.getText().toString());
        animal.put("phone", phoneView.getText().toString());
        animal.put("location", locationView.getText().toString());
        animal.put("latitude", Double.toString(location.getLatitude()));
        animal.put("longitude", Double.toString(location.getLongitude()));
        animal.put("type", typeSelection);
        animal.put("found", statusSelection);

        // Add user's device Firebase token
        animal.put("token", FirebaseInstanceId.getInstance().getToken());


        // Create new key for animal
        final DatabaseReference newAnimalRef = dataReference.push();
        String key = newAnimalRef.getKey();

        // Add animal's own key to its database entry
        animal.put("key", key);

        // Add animal to database
        newAnimalRef.setValue(animal);

        //TODO add picture functionality
        if (isImagePicked) {

            if (imageBmp != null) {
                StorageReference animalStorageRef = storageReference.child("server")
                                                                    .child("animals")
                                                                    .child("images")
                                                                    .child("thumb/" + key);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = animalStorageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri imageURL = taskSnapshot.getDownloadUrl();

                        newAnimalRef.child("thumbURL").setValue(imageURL.toString());

                    }
                });

            } else if (imageUri != null) {

                StorageReference animalStorageRef = storageReference.child("server")
                                                                    .child("animals")
                                                                    .child("images")
                                                                    .child("thumb/" + key);

                UploadTask uploadTask = animalStorageRef.putFile(imageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri imageURL = taskSnapshot.getDownloadUrl();

                        newAnimalRef.child("thumbURL").setValue(imageURL.toString());

                    }
                });

            } else Log.d("Image", "Unable to submit image reference");


        }

        // Add animal to local list of animals and save it to the data file
        yourAnimalList.add(animal.get("key").toString());
        FileUtils.writeToFile(yourAnimalList, getApplicationContext());

        return key;
    }



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
            Location l = null;
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                l = locationManager.getLastKnownLocation(provider);
            }



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



}