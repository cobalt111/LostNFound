package com.example.tim.lostnfound.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tim.lostnfound.R;
import com.example.tim.lostnfound.activity.Profile;
import com.example.tim.lostnfound.adapter.ListingsAdapter;
import com.example.tim.lostnfound.listener.RecyclerTouchListener;
import com.example.tim.lostnfound.utilities.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
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

    private Database mDatabase;
    private RecyclerView.LayoutManager layoutManager;
    private ListingsAdapter listingsAdapter;
    private List<String> yourAnimalList;


    // UI element for listing animals
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_your_pets, container, false);

        mDatabase = Database.getInstance();
        yourAnimalList = FileUtils.readFromFile(getContext());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(listingsAdapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        queryDatabaseToFillAdapter(yourAnimalList);
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
    private void queryDatabaseToFillAdapter(final List<String> savedAnimalList) {

        if (savedAnimalList != null) {

            mDatabase.readDataOnce(new Database.OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
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
                public void onStart() {

                }

                @Override
                public void onFailure(DatabaseError databaseError) {

                }
            });
        }
    }
}
