package com.example.tim.lostnfound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static android.content.Intent.EXTRA_TEXT;

public class ListingsFragment extends Fragment {


    private static final String ARG_SECTION_NUMBER = "section_number";

    private DatabaseReference dataReference;

    private HashMap<String, String> animal;
    private LinkedList<HashMap<String, String>> animalLinkedList;
    private LinkedList<String> nameLinkedList;
    private ListView listView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ListingsAdapter listingsAdapter;
    private String userIntention;
    private ArrayAdapter adapter;




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

        dataReference = DatabaseUtils.getReference(DatabaseUtils.getDatabase());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(listingsAdapter);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        listingsAdapter = queryDatabaseForData(userIntention);
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
    private ListingsAdapter queryDatabaseForData (String intention) {

        Query query = dataReference;

        if (intention != null) {
            query = dataReference.orderByChild("found").equalTo(intention);
        }


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<HashMap<String, String>> animalList = new ArrayList<>();

                for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
                    animal = (HashMap<String, String>) animalDatabaseEntry.getValue();

                    animalList.add(animal);

                }
                listingsAdapter = new ListingsAdapter(animalList);
                recyclerView.setAdapter(listingsAdapter);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return listingsAdapter;
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
                listingsAdapter = queryDatabaseForData(userIntention);
                return true;
            case R.id.found_animals:
                userIntention = "Found";
                listingsAdapter = queryDatabaseForData(userIntention);
                return true;
            case R.id.all_animals:
                userIntention = null;
                listingsAdapter = queryDatabaseForData(userIntention);
                return true;
        }
        return false;
    }

}
