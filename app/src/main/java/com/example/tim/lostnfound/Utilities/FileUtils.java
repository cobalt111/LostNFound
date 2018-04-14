package com.example.tim.lostnfound.Utilities;

import android.content.Context;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private final static String STORAGE_FILENAME = "lostNfound-animals.txt";
    private final static String FIREBASE_ID_FILENAME = "lostNfound-firebase-id.txt";

    public static File filePath(Context context){
        return new File(context.getFilesDir(), FileUtils.STORAGE_FILENAME);
    }

    // Verifies that the file exists, and if not, creates it
    public static boolean createFile(Context context){
        final File file = filePath(context);
        try{
            if (!file.isFile()){
                file.createNewFile();
                return true;
            } else return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Deletes outdated data and re-writes file with up-to-date data
    public static boolean writeToFile(List<String> list, Context context) {

        File file = filePath(context);

        if (file.isFile()) {

            try {
                file.delete();
                if (!createFile(context)){
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (!createFile(context)){
            return false;
        }

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(list);
            objectOutputStream.close();
            fileOutputStream.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    @SuppressWarnings("unchecked")
    public static List<String> readFromFile(Context context) {

        List<String> list = new ArrayList<>();

        File file = filePath(context);

        if (file.isFile()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                list = (List<String>) objectInputStream.readObject();

                fileInputStream.close();
                objectInputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return list;
    }

    public static void saveFirebaseID(Context context, String ID) {

        try {
            File file = new File(context.getFilesDir(), FileUtils.FIREBASE_ID_FILENAME);
            file.delete();
            file.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(ID);
            objectOutputStream.close();
            fileOutputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String readFirebaseID(Context context) {

        String firebaseID = "";

        try {
            File file = new File(context.getFilesDir(), FileUtils.FIREBASE_ID_FILENAME);

            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            firebaseID = (String) objectInputStream.readObject();

            fileInputStream.close();
            objectInputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return firebaseID;
    }
    
}
