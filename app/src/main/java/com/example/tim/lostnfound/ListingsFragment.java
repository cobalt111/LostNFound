package com.example.tim.lostnfound;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Intent.EXTRA_TEXT;

/**
 * Created by derekmesecar on 3/30/17.
 */

public class ListingsFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static ListingsFragment newInstance(int sectionNumber) {
        ListingsFragment fragment = new ListingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnMenuItemSelectedListener{
        public void onMenuItemSelected(String filter);
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





    private FirebaseDatabase mDatabase;
    private DatabaseReference ref;
    private HashMap<String, String> animal;
    private ArrayList<HashMap<String, String>> animalArrayList;
    private ArrayList<String> nameArrayList;
    private ListView listView;
    private Query query;
    private String userIntention;
    private ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_listings, container, false);


        mDatabase = DatabaseUtils.getDatabase();
        ref = mDatabase.getReference().child("server").child("animals");

        // just using namearraylist for the time being, will probably change to two line adapter
        listView = (ListView) rootView.findViewById(R.id.listview);
        animalArrayList = new ArrayList<>();
        nameArrayList = new ArrayList<>();

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
                                animal = animalArrayList.get(position);
                                listIntent.putExtra(EXTRA_TEXT, animal.get("key"));
                                startActivity(listIntent);

                            }
                        });
            }

        });


        setHasOptionsMenu(true);

        return rootView;
    }


    private ArrayAdapter queryDatabaseForData (String intention) {

        query = ref;
        animalArrayList = new ArrayList<>();
        nameArrayList = new ArrayList<>();

        if (intention != null) {
            query = ref.orderByChild("found").equalTo(intention);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()) {
                    animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
                    animalArrayList.add(animal);

                    if (animal.get("name").equals("")) {
                        nameArrayList.add(animal.get("type"));
                    } else nameArrayList.add(animal.get("name"));

                }

                adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, nameArrayList);
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



//        mDatabase = DatabaseUtils.getDatabase();
//        ref = mDatabase.getReference().child("server").child("animals");
//
//        // just using namearraylist for the time being
//        listView = (ListView) rootView.findViewById(R.id.listview);
//        animalArrayList = new ArrayList<>();
//        nameArrayList = new ArrayList<>();
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()){
//                    animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
//                    animalArrayList.add(animal);
//                    nameArrayList.add(animal.get("name"));
//                }
//
//                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, nameArrayList);
//                listView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("DEBUG", "Failure");
//            }
//        });
