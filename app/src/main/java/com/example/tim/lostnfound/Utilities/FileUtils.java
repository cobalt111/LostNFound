package com.example.tim.lostnfound.Utilities;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private final static String STORAGE_FILENAME = "lostNfound-animals.json";

    public static File filePath(Context context){
        return new File(context.getFilesDir(), FileUtils.STORAGE_FILENAME);
    }

    // Verifies that the file exists, and if not, create it
    public static boolean createFile(Context context){
        final File file = filePath(context);
        try{
            if (!file.isFile()){
                file.createNewFile();
                return true;
            }
            else return false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Deletes outdated data and re-writes file with up-to-date data
    public static boolean writeToFile(List<String> animalList, Context context) {

        File file = filePath(context);
        if (file.isFile()) {
            try {
                file.delete();
                if (!createFile(context)){
                    return false;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (!createFile(context)){
            return false;
        }

        try {
            final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            gson.toJson(animalList, bufferedWriter);
            bufferedWriter.close();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Overloaded for JsonObject argument
    public static boolean writeToFile(JsonObject animalList, Context context) {

        File file = filePath(context);
        if (file.isFile()) {
            try {
                file.delete();
                if (!createFile(context)){
                    return false;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (!createFile(context)){
            return false;
        }

        try {
            final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            gson.toJson(animalList, bufferedWriter);
            bufferedWriter.close();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> readFromFile(Context context) {

        List<String> animalList = new ArrayList<>();
        File file = filePath(context);
        if (file.isFile()){
            try {
                final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                animalList = gson.fromJson(bufferedReader, new TypeToken<List<String>>(){}.getType());
                bufferedReader.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return animalList;
    }

}
