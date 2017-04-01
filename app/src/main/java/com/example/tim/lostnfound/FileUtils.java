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

    final static String listOfYourPetsFile = "animal_key_list.txt";


    static void createFile(){
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(), FileUtils.listOfYourPetsFile);
        try{
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    static void replaceAnimalAsFound(HashMap<String, String> animal, File file) {
        try {
            LinkedList<HashMap<String, String>> linkedList = readFromFile(file);
            linkedList.remove(animal);
            animal.put("found", "Found");
            animal.put("notified", "false");
            linkedList.add(animal);
            writeToFile(linkedList, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void setNotifiedToTrue (HashMap<String, String> animal, File file) {
        try {
            LinkedList<HashMap<String, String>> linkedList = readFromFile(file);
            linkedList.remove(animal);
            animal.put("notified", "true");
            linkedList.add(animal);
            writeToFile(linkedList, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    static void hideAnimalInFile (HashMap<String, String> animal, File file) {
//        try {
//            linkedList<Animal> linkedList = readFromFile(file);
//            linkedList.remove(animal);
//            animal.put("hidden", "true");
//            linkedList.add(animal);
//            writeToFile(linkedList, file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



//    static void printContents(File file) {
//        linkedList<Animal> linkedList = new linkedList<>();
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
