package com.example.tim.lostnfound;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.HashMap;

class FileUtils {

    final static String listOfYourPetsFile = "lostNfound-animal_list.txt";


    // Verifies that the file exists, and if not, creates it
    static void createFile(){
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(), FileUtils.listOfYourPetsFile);
        try{
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Deletes outdated data and re-writes file with up-to-date data
    static void writeToFile(LinkedList<HashMap<String, String>> linkedList, File file) {
        try {
            file.delete();
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(linkedList);
            objectOutputStream.close();
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static LinkedList<HashMap<String, String>> readFromFile(File file) {
        LinkedList<HashMap<String, String>> linkedList = new LinkedList<>();

        if (file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                linkedList = (LinkedList<HashMap<String, String>>) objectInputStream.readObject();

                fileInputStream.close();
                objectInputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            createFile();
        }


        return linkedList;
    }

}
