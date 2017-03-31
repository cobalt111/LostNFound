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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static android.content.Intent.EXTRA_TEXT;
import static com.example.tim.lostnfound.R.id.container;

/**
 * Created by derekmesecar on 3/30/17.
 */

public class YourPetsFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static YourPetsFragment newInstance(int sectionNumber) {
        YourPetsFragment fragment = new YourPetsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_your_pets, container, false);

        File file = new File(getActivity().getExternalFilesDir(null).getAbsolutePath(), "animal_key_list.txt");

        LinkedList<HashMap<String, String>> animalLinkedList = FileUtils.readFromFile(file);
        final LinkedList<HashMap<String, String>> intentList = animalLinkedList;
        ArrayList<String> yourAnimalArrayList = new ArrayList<>();


        listView = (ListView) rootView.findViewById(R.id.listview);

        if (animalLinkedList.size() == 0) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Your list of lost animals is empty!", Toast.LENGTH_LONG);
            toast.show();
        } else {
            for (HashMap<String, String> animal : animalLinkedList) {
                yourAnimalArrayList.add(animal.get("name"));
            }
        }



        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, yourAnimalArrayList);
        listView.setAdapter(adapter);

        final Intent intent = new Intent(getActivity(), Profile.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                view.animate().setDuration(100).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                HashMap<String, String> animal = intentList.get(position);
                                intent.putExtra(EXTRA_TEXT, animal.get("key"));
                                //finish();
                                startActivity(intent);
                            }
                        });
            }

        });

        return rootView;
    }


}
