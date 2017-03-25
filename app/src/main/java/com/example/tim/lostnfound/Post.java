package com.example.tim.lostnfound;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Post extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("server/animals");

    private Button picButton;
    private Button submitButton;
    private TextView textView;
    private EditText editText;

    private String animalID;

    private SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        picButton = (Button) findViewById(R.id.postPictureButton);
        picButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO add picture functionality
            }
        });

        submitButton = (Button) findViewById(R.id.postSubmit);
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                animalID = onSubmitAnimal();

                editor.putString("animal_id", animalID);
                editor.commit();
            }
        });

    }



    private String onSubmitAnimal () {

        NewAnimal animal = new NewAnimal();
        DatabaseReference animalRef = ref.child("animals");

        editText = (EditText) findViewById(R.id.postName);
        animal.setName(textView.getText().toString());

        editText = (EditText) findViewById(R.id.postColor);
        animal.setColor(textView.getText().toString());

        editText = (EditText) findViewById(R.id.postDate);
        animal.setColor(textView.getText().toString());

        editText = (EditText) findViewById(R.id.postEmail);
        animal.setEmail(textView.getText().toString());

        editText = (EditText) findViewById(R.id.postDescription);
        animal.setLocation(textView.getText().toString());

        editText = (EditText) findViewById(R.id.postPhone);
        animal.setPhone(textView.getText().toString());

        //TODO add picture and type
        //editText = (TextView) findViewById(R.id.postType);
        //animal.setType(textView.getText().toString());

        animalRef.push().setValue(animal);
        String postID = animalRef.getKey();

        return postID;
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
