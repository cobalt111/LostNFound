package com.example.tim.lostnfound;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    FirebaseDatabase mDatabase;
    DatabaseReference ref;
    HashMap<String, String> animal;
    ArrayList<HashMap<String, String>> animalArrayList;
    ArrayList<String> nameArrayList;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_listings, container, false);

        mDatabase = DatabaseUtils.getDatabase();
        ref = mDatabase.getReference().child("server").child("animals");

        // just using namearraylist for the time being
        listView = (ListView) rootView.findViewById(R.id.listview);
        animalArrayList = new ArrayList<>();
        nameArrayList = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot animalDatabaseEntry : dataSnapshot.getChildren()){
                    animal = (HashMap<String, String>) animalDatabaseEntry.getValue();
                    animalArrayList.add(animal);
                    nameArrayList.add(animal.get("name"));
                }

                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, nameArrayList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DEBUG", "Failure");
            }
        });

        return rootView;
    }
}
