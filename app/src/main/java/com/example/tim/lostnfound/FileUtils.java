package com.example.tim.lostnfound;


import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import android.content.Context;

class FileUtils {


//    static File createFile(){
//        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(), "animal_key_list.txt");
//        try{
//            file.createNewFile();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } return file;
//    }

    static void writeToFile(LinkedList<HashMap<String, String>> linkedList, File file) {
        try {
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
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            linkedList = (LinkedList<HashMap<String, String>>) objectInputStream.readObject();

            fileInputStream.close();
            objectInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return linkedList;
    }

    static void resetFile(File file) {
        try {
            file.delete();
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    static void printContents(File file) {
//        LinkedList<HashMap<String, String>> linkedList = new LinkedList<>();
//
//        createFile();
//        linkedList = readFromFile(file);
//
//
//        for (HashMap<String, String> animal : linkedList) {
//            System.out.println(Arrays.asList(animal));
//        }
//    }


}
