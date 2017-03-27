package com.example.tim.lostnfound;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.example.tim.lostnfound.DatabaseUtils;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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


    FirebaseDatabase database;
    private DatabaseReference ref;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private String prefKeyOne;
    private String prefKeyTwo;
    private String prefKeyThree;
    private String prefKeyFour;
    private String prefKeyFive;

    private String lastUsedKey;

    private boolean[] usedKeys;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // to keep track of which keys are taken by lost animals
        usedKeys = new boolean[5];
        prefKeyOne = "prefKeyOne";
        prefKeyTwo = "prefKeyTwo";
        prefKeyThree = "prefKeyThree";
        prefKeyFour = "prefKeyFour";
        prefKeyFive = "prefKeyFive";

        sharedPreferences = this.getSharedPreferences("com.example.tim.lostnfound", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        database = DatabaseUtils.getDatabase();
        ref = database.getReference("server");


        Button picButton = (Button) findViewById(R.id.postPictureButton);
        picButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO add picture functionality
            }
        });

        Button submitButton = (Button) findViewById(R.id.postSubmit);

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String animalID = onSubmitAnimal();
                lastUsedKey = assignKey(animalID);

                if (lastUsedKey != null) {
                    editor.commit();
                } else System.out.println("All keys are taken");

                Toast toast = Toast.makeText(getApplicationContext(), "Your lost pet has been posted!", LENGTH_LONG);
                toast.show();

                Intent intent = new Intent(Post.this, Profile.class);
                intent.putExtra(EXTRA_TEXT, lastUsedKey);
                finish();
                startActivity(intent);

            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }



    private String onSubmitAnimal () {

        EditText editText;
        Map<String, String> animal = new HashMap<>();
        DatabaseReference animalRef = ref.child("animals");

        editText = (EditText) findViewById(R.id.postName);
        animal.put("name", editText.getText().toString());

        editText = (EditText) findViewById(R.id.postColor);
        animal.put("color", editText.getText().toString());

        editText = (EditText) findViewById(R.id.postDate);
        animal.put("date", editText.getText().toString());

        editText = (EditText) findViewById(R.id.postEmail);
        animal.put("email", editText.getText().toString());

        editText = (EditText) findViewById(R.id.postDescription);
        animal.put("description", editText.getText().toString());

        editText = (EditText) findViewById(R.id.postPhone);
        animal.put("phone", editText.getText().toString());

        editText = (EditText) findViewById(R.id.postLocation);
        animal.put("location", editText.getText().toString());

        //TODO add picture and type
        //editText = (TextView) findViewById(R.id.postType);
        //animal.setType(textView.getText().toString());

        DatabaseReference newAnimalRef = animalRef.push();
        newAnimalRef.setValue(animal);

        return newAnimalRef.getKey();
    }

    private int determineUnusedKey(boolean[] boolArr){
        int index = 0;
        for (boolean current : boolArr) {
            if (!current) {
                return index;
            } else index++;
        } return 5;
    }

    private String assignKey(String animalID) {
        switch (determineUnusedKey(usedKeys)) {
            case 0:
                editor.putString("com.example.tim.lostnfound." + prefKeyOne, animalID);
                return prefKeyOne;
            case 1:
                editor.putString("com.example.tim.lostnfound." + prefKeyTwo, animalID);
                return prefKeyTwo;
            case 2:
                editor.putString("com.example.tim.lostnfound." + prefKeyThree, animalID);
                return prefKeyThree;
            case 3:
                editor.putString("com.example.tim.lostnfound." + prefKeyFour, animalID);
                return prefKeyFour;
            case 4:
                editor.putString("com.example.tim.lostnfound." + prefKeyFive, animalID);
                return prefKeyFive;
            default:
                return null;
        }
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
