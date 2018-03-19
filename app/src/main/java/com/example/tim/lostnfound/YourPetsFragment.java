package com.example.tim.lostnfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.LinkedList;
import java.util.HashMap;

import static android.content.Intent.EXTRA_TEXT;


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

    // UI element for listing animals
    private ListView listView;

    // animalLinkedList is used to retrieve the animal data from the data file
    private LinkedList<HashMap<String, String>> animalLinkedList;

    // yourAnimalLinkedList is a list of animal names used to populate the listView with names
    private LinkedList<String> yourAnimalLinkedList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_your_pets, container, false);

        // Verify file exists. If not, create it. Import data from it to animalLinkedList (if any)
        FileUtils.createFile();
        File file = new File(getContext().getExternalFilesDir(null).getAbsolutePath(), FileUtils.listOfYourPetsFile);
        animalLinkedList = FileUtils.readFromFile(file);




        yourAnimalLinkedList = new LinkedList<>();

        listView = (ListView) rootView.findViewById(R.id.listview);

        // Create LinkedList of every animal in the HashMap to be entered into the following adapter
        for (HashMap<String, String> animal : animalLinkedList) {
            yourAnimalLinkedList.add(animal.get("name"));
        }

        // Enter list of your animals into the adapter and set the adapter to the listView
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, yourAnimalLinkedList);
        listView.setAdapter(adapter);

        // If one of the animals is selected, this intent will be used to open its profile
        final Intent intent = new Intent(getActivity(), Profile.class);

        // intentList is used for finding the selected animal key once selected in the listView
        final LinkedList<HashMap<String, String>> intentList = animalLinkedList;

        // Listener for the listView. The position of the selected animal in the listView corresponds with the position in the intentList
        // The position is found, and the key of the respective animal is retrieved according to it
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                view.animate().setDuration(0).alpha(1)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                HashMap<String, String> animal = intentList.get(position);
                                intent.putExtra(EXTRA_TEXT, animal.get("key"));
                                startActivity(intent);
                            }
                        });
            }

        });


        return rootView;
    }

}
