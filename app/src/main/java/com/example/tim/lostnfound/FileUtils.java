package com.example.tim.lostnfound;

import android.content.Context;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

class FileUtils {

    private final static String STORAGE_FILENAME = "lostNfound-animal_list.txt";

    // TODO figure out how to serve up the file functions properly

    static File filePath(Context context){
        return new File(context.getFilesDir(), FileUtils.STORAGE_FILENAME);
    }

    // Verifies that the file exists, and if not, creates it
    static boolean createFile(Context context){
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
    static boolean writeToFile(List<String> list, Context context) {

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



    static List<String> readFromFile(Context context) {

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
    
}
