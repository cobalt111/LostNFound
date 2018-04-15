package com.example.tim.lostnfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.tim.lostnfound.Utilities.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListingsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private Database mDatabase;
    private HashMap<String, String> animal;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ListingsAdapter listingsAdapter;
    private String userIntention;

    public static ListingsFragment newInstance(int sectionNumber) {
        ListingsFragment fragment = new ListingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnMenuItemSelectedListener{
        void onMenuItemSelected(String filter);
    }

    OnMenuItemSelectedListener onMenuItemSelectedListener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            onMenuItemSelectedListener = (OnMenuItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_listings, container, false);

        mDatabase = Database.getInstance();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(listingsAdapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        queryDatabaseToFillAdapter(userIntention);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent openProfileIntent = new Intent(getActivity(), Profile.class);
                openProfileIntent.putExtra("animalID", listingsAdapter.animalList
                                                                            .get(position)
                                                                            .get("key"));

                startActivity(openProfileIntent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        setHasOptionsMenu(true);
        return rootView;
    }

    @SuppressWarnings("unchecked")
    private void queryDatabaseToFillAdapter (String intention) {

        Query query = mDatabase.getDatabaseReference();

        if (intention != null) {
            query = mDatabase.getDatabaseReference().orderByChild("found").equalTo(intention);
        }

        mDatabase.readDataContinuously(query, new Database.OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                List<HashMap<String, String>> animalList = new ArrayList<>();

                for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
                    animal = (HashMap<String, String>) animalDatabaseEntry.getValue();

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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.listings_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.lost_animals:
                userIntention = "Lost";
                queryDatabaseToFillAdapter(userIntention);
                return true;
            case R.id.found_animals:
                userIntention = "Found";
                queryDatabaseToFillAdapter(userIntention);
                return true;
            case R.id.all_animals:
                queryDatabaseToFillAdapter(null);
                return true;
        }
        return false;
    }

}
