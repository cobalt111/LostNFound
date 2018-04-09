package com.example.tim.lostnfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class YourPetsFragment extends Fragment {



    private static final String ARG_SECTION_NUMBER = "section_number";

    // For fragment switching
    public static YourPetsFragment newInstance(int sectionNumber) {
        YourPetsFragment fragment = new YourPetsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private DatabaseReference dataReference;
    private RecyclerView.LayoutManager layoutManager;
    private ListingsAdapter listingsAdapter;
    private List<String> yourAnimalList;


    // UI element for listing animals
    private RecyclerView recyclerView;



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_your_pets, container, false);

        dataReference = DatabaseUtils.getReference(DatabaseUtils.getDatabase());
        yourAnimalList = FileUtils.readFromFile(getContext());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(listingsAdapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        listingsAdapter = queryDatabaseForData(yourAnimalList);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {

                Intent openProfileIntent = new Intent(getActivity(), Profile.class);
                openProfileIntent
                        .putExtra("animalID", listingsAdapter.animalList
                        .get(position)
                        .get("key"));

                openProfileIntent.putExtra("yourpet", "true");

                startActivity(openProfileIntent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        return rootView;
    }


    @SuppressWarnings("unchecked")
    private ListingsAdapter queryDatabaseForData(final List<String> savedAnimalList) {

        if (savedAnimalList != null) {
            dataReference.addListenerForSingleValueEvent((new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    List<HashMap<String, String>> animalList = new ArrayList<>();
                    HashMap<String, String> animal;

                    for (String savedAnimal : savedAnimalList) {

                        animal = (HashMap<String, String>) dataSnapshot.child(savedAnimal).getValue();
                        animalList.add(animal);
                    }

                    listingsAdapter = new ListingsAdapter(animalList);
                    recyclerView.setAdapter(listingsAdapter);
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }));
        }


        return listingsAdapter;
    }

}
