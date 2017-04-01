package com.example.tim.lostnfound;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
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

    private ListView listView;
    private LinkedList<String> yourAnimalLinkedList;
    private LinkedList<HashMap<String, String>> animalLinkedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_your_pets, container, false);


        FileUtils.createFile();
        File file = new File(getContext().getExternalFilesDir(null).getAbsolutePath(), FileUtils.listOfYourPetsFile);
        animalLinkedList = FileUtils.readFromFile(file);
        final LinkedList<HashMap<String, String>> intentList = animalLinkedList;

        yourAnimalLinkedList = new LinkedList<>();

        listView = (ListView) rootView.findViewById(R.id.listview);

        for (HashMap<String, String> animal : animalLinkedList) {
            yourAnimalLinkedList.add(animal.get("name"));
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, yourAnimalLinkedList);
        listView.setAdapter(adapter);

        final Intent intent = new Intent(getActivity(), Profile.class);
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
                                //finish();
                                startActivity(intent);
                            }
                        });
            }

        });


        return rootView;
    }

//    public static void saveToFile(String filename, LinkedList<HashMap<String, String>> linkedList, Context context) {
//
//        FileOutputStream fileOutputStream;
//        try {
//            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
//
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//            objectOutputStream.writeObject(linkedList);
//            objectOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

//    private static LinkedList<HashMap<String,String>> readFromFile(String filename, Context context) {
//        LinkedList<HashMap<String, String>> linkedList = new LinkedList<>();
//
//        FileInputStream fileInputStream;
//        try {
//            fileInputStream = context.openFileInput(filename);
//
//
//            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
//            linkedList = (LinkedList<HashMap<String,String>>) objectInputStream.readObject();
//            objectInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return linkedList;
//
//    }



}
