package com.example.tim.lostnfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.ValueEventListener;

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

    // UI element for listing animals
    private ListView listView;
    private ArrayAdapter adapter;

    // animalLinkedList is used to retrieve the animal data from the data file
    private List<String> animalLinkedList;

    // animalNameLinkedList is a list of animal names used to populate the listView with names
    private List<String> animalNameLinkedList;





    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_your_pets, container, false);
        listView = (ListView) rootView.findViewById(R.id.listview);


        // Verify file exists. If not, create it. Import data from it to animalLinkedList (if any)
        FileUtils.createFile(getContext());
        animalLinkedList = FileUtils.readFromFile(getContext());
        animalNameLinkedList = new LinkedList<>();

        // The objective of this code block is to retrieve name of every animal in animalLinkedList
        // so that the animalNameLinkedList will have the names to fill the adapter with
        if (animalLinkedList.size() > 0) {
            
            // Create reference to database
            // Find the particular animal in the database according to the animalID
            dataReference = DatabaseUtils.getReference(DatabaseUtils.getDatabase());

            // Contact database, retrieve data elements and display them in the appropriate view
            dataReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (String animalID : animalLinkedList) {
                        animalNameLinkedList.add(dataSnapshot.child(animalID).child("name").getValue().toString());
                    }
                    adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, animalNameLinkedList);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("DEBUG", "Failure");
                }
            });
        }


        // Enter list of your animals into the adapter and set the adapter to the listView
        if (adapter == null) {
            adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, animalNameLinkedList);
        }
        listView.setAdapter(adapter);


        // If one of the animals is selected from the list, this intent will be used to open its profile
        final Intent intent = new Intent(getActivity(), YourPetsProfile.class);

        // Listener for the listView. The position of the selected animal in the listView corresponds
        // with the position in the intentList. The position is found in the list, the key of the
        // respective animal is retrieved, and the key is added to the intent
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                view.animate().setDuration(0).alpha(1)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                String animalID = animalLinkedList.get(position);
                                intent.putExtra("animalID", animalID);
                                intent.putExtra("userSubmitted", "true");
                                startActivity(intent);
                            }
                        });
            }

        });


        return rootView;
    }

}
