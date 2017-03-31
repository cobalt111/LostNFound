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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_listings, container, false);

//        Intent intent = getActivity().getIntent();
//        if (intent.hasExtra("filter")) {
//            userIntention = intent.getStringExtra("filter");
//        }

        Bundle intentionBundle = getArguments();
        if (intentionBundle != null) {
            userIntention = intentionBundle.getString("filter");
        }



        mDatabase = DatabaseUtils.getDatabase();
        ref = mDatabase.getReference().child("server").child("animals");
        query = ref;

        // just using namearraylist for the time being, will probably change to two line adapter
        listView = (ListView) rootView.findViewById(R.id.listview);
        animalArrayList = new ArrayList<>();
        nameArrayList = new ArrayList<>();


        if (userIntention != null) {
            query = ref.orderByChild("found").equalTo(userIntention);
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


                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, nameArrayList);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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
        setHasOptionsMenu(true);

        return rootView;
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
                onMenuItemSelectedListener.onMenuItemSelected(userIntention);


                return true;
            case R.id.found_animals:
                Bundle foundBundle = new Bundle();
                String found = "Found";
                foundBundle.putString("filter", found );
                ListingsFragment foundFragment = new ListingsFragment();
                foundFragment.setArguments(foundBundle);

                FragmentTransaction foundTransaction = getFragmentManager().beginTransaction();
                foundTransaction.detach(this).attach(this).commit();
                return true;
            case R.id.all_animals:
//                String noFilter = null;
//                Intent noFilterIntent = new Intent(this, Listings.class);
//                noFilterIntent.putExtra("filter", noFilter);
//                mViewPager.setCurrentItem(1);

        }
        return false;
    }


}
