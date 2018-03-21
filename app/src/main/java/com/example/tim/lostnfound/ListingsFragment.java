package com.example.tim.lostnfound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedList;

import static android.content.Intent.EXTRA_TEXT;

public class ListingsFragment extends Fragment {


    private static final String ARG_SECTION_NUMBER = "section_number";

    private FirebaseDatabase mDatabase;
    private DatabaseReference dataReference;
    private HashMap<String, String> animal;
    private LinkedList<HashMap<String, String>> animalLinkedList;
    private LinkedList<String> nameLinkedList;
    private ListView listView;
    private Query query;
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

        mDatabase = DatabaseUtils.getDatabase();
        dataReference = DatabaseUtils.getReference(mDatabase);

        // just using namearraylist for the time being, will probably change to two line adapter
        listView = (ListView) rootView.findViewById(R.id.listview);


        adapter = queryDatabaseForData(userIntention);


        final Intent listIntent = new Intent(getContext(), Profile.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {

                view.animate().setDuration(0).alpha(1)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                animal = animalLinkedList.get(position);
                                listIntent.putExtra("animalID", animal.get("key"));
                                startActivity(listIntent);

                            }
                        });
            }

        });


        setHasOptionsMenu(true);

        return rootView;
    }

    @SuppressWarnings("unchecked")
    private ArrayAdapter queryDatabaseForData (String intention) {

        query = dataReference;
        animalLinkedList = new LinkedList<>();
        nameLinkedList = new LinkedList<>();

        if (intention != null) {
            query = dataReference.orderByChild("found").equalTo(intention);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
                    animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
                    animalLinkedList.add(animal);

                    if (animal.get("name").equals("")) {
                        nameLinkedList.add(animal.get("type"));
                    } else nameLinkedList.add(animal.get("name"));

                }

                adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, nameLinkedList);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return adapter;
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
                adapter = queryDatabaseForData(userIntention);
                return true;
            case R.id.found_animals:
                userIntention = "Found";
                adapter = queryDatabaseForData(userIntention);
                return true;
            case R.id.all_animals:
                userIntention = null;
                adapter = queryDatabaseForData(userIntention);
                return true;
        }
        return false;
    }

}
