package com.example.tim.lostnfound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.tim.lostnfound.DatabaseUtils;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static android.content.Intent.EXTRA_TEXT;

public class Profile extends AppCompatActivity {



    private DatabaseReference dataReference;
    private FirebaseStorage storage;
//    private StorageReference storageReference;

    private String animalID;
    private boolean isOwnedAnimal;
    private boolean isFromEditInstance;
    private boolean isAnimalFound;


    // Declare UI elements
    private TextView nameView;
    private TextView colorView;
    private TextView dateView;
    private TextView descView;
    private TextView locationView;
    private TextView phoneView;
    private TextView emailView;
    private TextView statusView;
    private TextView typeView;
    private ImageButton changeToFoundButton;
    private ImageButton changeToLostButton;

    private ImageView imageView;

    // new buttons for capstone
    private ImageButton removeListingButton;
    private ImageButton editListingButton;


    public interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }

    public void readDataContinuously(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });

    }

    public void readDataOnce(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        setTitle("Animal Profile");

        isOwnedAnimal = false;
        isFromEditInstance = false;
        isAnimalFound = false;

        dataReference = DatabaseUtils.getReference(DatabaseUtils.getDatabase());
        storage = FirebaseStorage.getInstance();



        Intent intent = getIntent();
        if (intent.hasExtra("animalID")) {
            animalID = intent.getStringExtra("animalID");
        }

        if (intent.hasExtra("yourpet")) {
            if (intent.getStringExtra("yourpet").equals("true")) {
                isOwnedAnimal = true;
            }
        }

        if (intent.hasExtra("fromEditInstance") && intent.getStringExtra("fromEditInstance").equals("true")) {
            isFromEditInstance = true;
        }


        // Initialize UI elements
        nameView = (TextView) findViewById(R.id.profileName);
        colorView = (TextView) findViewById(R.id.profileColor);
        dateView = (TextView) findViewById(R.id.profileDate);
        descView = (TextView) findViewById(R.id.profileDescription);
        locationView = (TextView) findViewById(R.id.profileLocation);
        phoneView = (TextView) findViewById(R.id.profilePhone);
        emailView = (TextView) findViewById(R.id.profileEmail);
        statusView = (TextView) findViewById(R.id.profileFound);
        typeView = (TextView) findViewById(R.id.profileType);
        imageView = (ImageView) findViewById(R.id.profileImage);
        editListingButton = (ImageButton) findViewById(R.id.profileEditButton);
        changeToFoundButton = (ImageButton) findViewById(R.id.profileChangeToFoundButton);
        changeToLostButton = (ImageButton) findViewById(R.id.profileChangeToLostButton);
        removeListingButton = (ImageButton) findViewById(R.id.profileRemoveButton);

        editListingButton.setVisibility(View.GONE);
        removeListingButton.setVisibility(View.GONE);

        changeToLostButton.setVisibility(View.GONE);
        changeToFoundButton.setVisibility(View.GONE);


        if (isOwnedAnimal || isFromEditInstance) {

            editListingButton.setVisibility(View.VISIBLE);
            editListingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent editProfileIntent = new Intent(Profile.this, Post.class);

                    editProfileIntent.putExtra("animalID", animalID);
                    editProfileIntent.putExtra("editInstance", "true");
                    finish();
                    startActivity(editProfileIntent);


                }
            });


            removeListingButton.setVisibility(View.VISIBLE);
            removeListingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();

                    dataReference.removeValue();

                    List<String> yourAnimalList = FileUtils.readFromFile(getApplicationContext());
                    yourAnimalList.remove(animalID);
                    FileUtils.writeToFile(yourAnimalList, getApplicationContext());

                    Toast toast = Toast.makeText(getApplicationContext(), "Listing deleted", Toast.LENGTH_LONG);
                    toast.show();



                }
            });
        }


        // TODO fix buttons to reflect Profile button usage


        // Initialize button and listener
        changeToFoundButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                readDataOnce(dataReference, new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        String status = (String) dataSnapshot.child("found").getValue();

                        if (status.equals("Lost")) {
                            dataReference.child("found").setValue("Found");
                            Toast toast = Toast.makeText(getApplicationContext(), "Changed status to found", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Animal already listed as lost", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFailure() {

                    }
                });

            }
        });

        changeToLostButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                readDataOnce(dataReference, new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        String status = (String) dataSnapshot.child("found").getValue();
                        if (status.equals("Found")) {
                            dataReference.child("found").setValue("Lost");

                            Toast toast = Toast.makeText(getApplicationContext(), "Changed status to lost", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Animal already listed as found", Toast.LENGTH_SHORT);
                            toast.show();

                        }

                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFailure() {

                    }
                });

            }
        });


        // Find the particular animal in the database according to the animalID passed in the intent
        dataReference = dataReference.child(animalID);


        readDataContinuously(dataReference, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                @SuppressWarnings("unchecked")
                HashMap<String, Object> animal = (HashMap<String, Object>) dataSnapshot.getValue();

                if (animal != null) {
                    if (animal.get("found").toString().equals("Found")){
                        isAnimalFound = true;
                        changeToLostButton.setVisibility(View.VISIBLE);
                        changeToFoundButton.setVisibility(View.GONE);



                    }
                    else {
                        isAnimalFound = false;
                        changeToFoundButton.setVisibility(View.VISIBLE);
                        changeToLostButton.setVisibility(View.GONE);


                    }

                    // TODO add all these strings to the strings.xml instead

                    if (animal.get("name").equals("")){
                        nameView.setText("(none listed)");
                    } else {
                        nameView.setText(animal.get("name").toString());
                    }

                    if (animal.get("color").equals("")){
                        colorView.setText("(none listed)");
                    } else {
                        colorView.setText(animal.get("color").toString());
                    }

                    if (animal.get("date").equals("")){
                        dateView.setText("(none listed)");
                    } else {
                        dateView.setText(animal.get("date").toString());
                    }

                    if (animal.get("description").equals("")){
                        descView.setText("(none listed)");
                    } else {
                        descView.setText(animal.get("description").toString());
                    }

                    if (animal.get("location").equals("")){
                        locationView.setText("(none listed)");
                    } else {
                        locationView.setText(animal.get("location").toString());
                    }

                    if (animal.get("phone").equals("")){
                        phoneView.setText("(none listed)");
                    } else {
                        phoneView.setText(animal.get("phone").toString());
                    }

                    if (animal.get("email").equals("")){
                        emailView.setText("(none listed)");
                    } else {
                        emailView.setText(animal.get("email").toString());
                    }

                    typeView.setText(animal.get("type").toString());

                    statusView.setText(animal.get("found").toString());


                    if (animal.get("thumbURL") != null) {
                        Glide
                                .with(getApplicationContext())
                                .load(animal.get("thumbURL").toString()) // the uri you got from Firebase
                                .into(imageView); //Your imageView variable
//                    Picasso.get()
//                            .load(animal.get("thumbURL").toString())
//                            .resize(250,200)
//                            .into(imageView);

                    } else {

                        Drawable myDrawable;
                        switch (animal.get("type").toString()) {
                            case "Dog":
                                myDrawable = getResources().getDrawable(R.drawable.lnf_dog);
                                imageView.setImageDrawable(myDrawable);
                                break;
                            case "Cat":
                                myDrawable = getResources().getDrawable(R.drawable.lnf_cat);
                                imageView.setImageDrawable(myDrawable);
                                break;
                            case "Bird":
                                myDrawable = getResources().getDrawable(R.drawable.lnf_bird);
                                imageView.setImageDrawable(myDrawable);
                                break;
                            case "Ferret":
                                myDrawable = getResources().getDrawable(R.drawable.lnf_ferret);
                                imageView.setImageDrawable(myDrawable);
                                break;
                            case "Hamster/Guinea Pig":
                                myDrawable = getResources().getDrawable(R.drawable.lnf_hamster);
                                imageView.setImageDrawable(myDrawable);
                                break;
                            case "Mouse/Rat":
                                myDrawable = getResources().getDrawable(R.drawable.lnf_mouse);
                                imageView.setImageDrawable(myDrawable);
                                break;
                            case "Snake/Lizard":
                                myDrawable = getResources().getDrawable(R.drawable.lnf_snake);
                                imageView.setImageDrawable(myDrawable);
                                break;
                            case "Other":
                                myDrawable = getResources().getDrawable(R.drawable.lnf_other);
                                imageView.setImageDrawable(myDrawable);
                                break;
                            default:
                                myDrawable = getResources().getDrawable(R.drawable.lnf_other);
                                imageView.setImageDrawable(myDrawable);
                                break;
                        }
                    }
                }

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });


    }
}
