package com.example.tim.lostnfound;



import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.example.tim.lostnfound.FileUtils;
import com.google.firebase.database.FirebaseDatabase;


import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;


import static android.content.Intent.EXTRA_TEXT;
import static android.widget.Toast.LENGTH_LONG;


public class Post extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome = new Intent(Post.this, MainActivity.class);
                    finish();
                    startActivity(intentHome);
                    return true;
                case R.id.navigation_listings:
                    Intent intentListings = new Intent(Post.this, Listings.class);
                    finish();
                    startActivity(intentListings);
                    return true;
                case R.id.navigation_animals:
                    Intent intentAnimals = new Intent(Post.this, YourPets.class);
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


    private FirebaseDatabase database;
    private DatabaseReference ref;

    private LinkedList<HashMap<String, String>> yourAnimalList;

    private Spinner typeDropdown;
    private Spinner statusDropdown;
    private Button locationButton;
    private String typeSelection;
    private String statusSelection;
    private Button submitButton;
    private String mapLocation;

    private EditText nameView;
    private EditText colorView;
    private EditText dateView;
    private EditText descView;
    private EditText locationView;
    private EditText phoneView;
    private EditText emailView;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

//        Intent intent = getIntent();
//        if (intent.getStringExtra("status") == "lost") {
//            userPostIntention = "lost";
//        } else if (intent.getStringExtra("status") == "found") {
//            userPostIntention = "found";
//        } else userPostIntention = null;

        File file = new File(getExternalFilesDir(null).getAbsolutePath(), "animal_key_list.txt");
        yourAnimalList = FileUtils.readFromFile(file);

        database = DatabaseUtils.getDatabase();
        ref = database.getReference("server");

        nameView = (EditText) findViewById(R.id.postName);
        colorView = (EditText) findViewById(R.id.postColor);
        dateView = (EditText) findViewById(R.id.postDate);
        emailView = (EditText) findViewById(R.id.postEmail);
        descView = (EditText) findViewById(R.id.postDescription);
        phoneView = (EditText) findViewById(R.id.postPhone);
        locationView = (EditText) findViewById(R.id.postLocation);


        // dropdown type selection spinner
        typeDropdown = (Spinner) findViewById(R.id.post_type_spinner);
        final String[] typesList = {"Dog", "Cat", "Hamster/Guinea Pig", "Mouse/Rat", "Bird", "Snake/Lizard", "Ferret", "Other"};
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

        // dropdown status selection spinner
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
                typeSelection = null;
            }
        });

        locationButton = (Button) findViewById(R.id.postPictureButton);
        locationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });



        // submit button
        submitButton = (Button) findViewById(R.id.postSubmit);
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if (mapLocation != null) {
                    String animalID = onSubmitAnimal();

                    Toast toast = Toast.makeText(getApplicationContext(), "Your lost pet has been posted!", LENGTH_LONG);
                    toast.show();

                    Intent intent = new Intent(Post.this, Profile.class);
                    intent.putExtra(EXTRA_TEXT, animalID);
                    finish();
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "You haven't set a location yet!", LENGTH_LONG);
                    toast.show();
                }


            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }



    private String onSubmitAnimal () {

        HashMap<String, String> animal = new HashMap<>();
        DatabaseReference animalRef = ref.child("animals");

        animal.put("name", nameView.getText().toString());
        animal.put("color", colorView.getText().toString());
        animal.put("date", dateView.getText().toString());
        animal.put("email", emailView.getText().toString());
        animal.put("description", descView.getText().toString());
        animal.put("phone", phoneView.getText().toString());
        animal.put("location", locationView.getText().toString());
        animal.put("map_location", )
        animal.put("type", typeSelection);
        animal.put("found", statusSelection);

        if (statusSelection == "Found") {
            animal.put("notified", "true");
        } else animal.put("notified", "false");


        //TODO add picture functionality?

        DatabaseReference newAnimalRef = animalRef.push();
        animal.put("key", newAnimalRef.getKey());
        newAnimalRef.setValue(animal);

        yourAnimalList.add(animal);
        File file = new File(getExternalFilesDir(null).getAbsolutePath(), "animal_key_list.txt");
        FileUtils.writeToFile(yourAnimalList, file);

        return newAnimalRef.getKey();
    }






    // for taking the pictures
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    // for saving the image with a new filename
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

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


// old code


//    private String prefKeyOne;
//    private String prefKeyTwo;
//    private String prefKeyThree;
//    private String prefKeyFour;
//    private String prefKeyFive;
//    private String lastUsedKey;
//
//    private boolean[] usedKeys;
//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;

//                editor.putString("com.example.tim.lostnfound., animalID);
//                editor.commit();
//                } else System.out.println("All keys are taken");


//        File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "animal_key_list.txt");
//        file1.getParentFile().mkdirs();
//        try {
//            int permissionCheck = ContextCompat.checkSelfPermission(Post.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//                if (!file1.createNewFile()) {
//                    Log.i("Test", "This file is already exist: " + file1.getAbsolutePath());
//                }
//            }
//
//            FileOutputStream fileOutputStream = new FileOutputStream(file1);
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


// to keep track of which keys are taken by lost animals
//        usedKeys = new boolean[5];
//        prefKeyOne = "prefKeyOne";
//        prefKeyTwo = "prefKeyTwo";
//        prefKeyThree = "prefKeyThree";
//        prefKeyFour = "prefKeyFour";
//        prefKeyFive = "prefKeyFive";

//        sharedPreferences = this.getSharedPreferences("com.example.tim.lostnfound", Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();


//    private int determineUnusedKey(boolean[] boolArr){
//        int index = 0;
//        for (boolean current : boolArr) {
//            if (!current) {
//                return index;
//            } else index++;
//        } return 5;
//    }
//
//    private String assignKey(String animalID) {
//        switch (determineUnusedKey(usedKeys)) {
//            case 0:
//                return prefKeyOne;
//            case 1:
//                editor.putString("com.example.tim.lostnfound." + prefKeyTwo, animalID);
//                return prefKeyTwo;
//            case 2:
//                editor.putString("com.example.tim.lostnfound." + prefKeyThree, animalID);
//                return prefKeyThree;
//            case 3:
//                editor.putString("com.example.tim.lostnfound." + prefKeyFour, animalID);
//                return prefKeyFour;
//            case 4:
//                editor.putString("com.example.tim.lostnfound." + prefKeyFive, animalID);
//                return prefKeyFive;
//            default:
//                return null;
//        }
//    }