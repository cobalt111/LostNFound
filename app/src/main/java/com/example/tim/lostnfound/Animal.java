package com.example.tim.lostnfound;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by Tim on 4/1/2017.
 */

class Animal implements Parcelable {

    private String name;
    private String appearance;
    private String date;
    private String location;
    private String email;
    private String description;
    private String phone;
    private String type;
    private String found;
    private String key;
    private String notified;
//    private HashMap<String, String> hashMap;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        HashMap<String, String> animalHashMap = new HashMap<>();

        dest.writeString(name);
        dest.writeString(appearance);
        dest.writeString(date);
        dest.writeString(location);
        dest.writeString(email);
        dest.writeString(description);
        dest.writeString(phone);
        dest.writeString(type);
        dest.writeString(found);
        dest.writeString(key);
        dest.writeString(notified);

//        animalHashMap.put("name", name);
//        animalHashMap.put("color", appearance);
//        animalHashMap.put("date", date);
//        animalHashMap.put("location", location);
//        animalHashMap.put("email", email);
//        animalHashMap.put("description", description);
//        animalHashMap.put("phone", phone);
//        animalHashMap.put("found", found);
//        animalHashMap.put("notified", notified);
//        animalHashMap.put("key", key);
//        animalHashMap.put("type", type);
//
//        dest.writeSerializable(animalHashMap);

    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Animal createFromParcel(Parcel in) {
            return new Animal(in);
        }

        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };

    // "De-parcel object
    public Animal(Parcel in) {

        name = in.readString();
        appearance = in.readString();
        date = in.readString();
        location = in.readString();
        email = in.readString();
        description = in.readString();
        phone = in.readString();
        type = in.readString();
        found = in.readString();
        key = in.readString();
        notified = in.readString();
    }

    public Animal() {

    }
//
//    public HashMap<String, String> getHashMap() {
//        return hashMap;
//    }
//
//    public void setHashMap(HashMap<String, String> hashMap) {
//        this.hashMap = hashMap;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
//        hashMap.put("name", name);
    }

    public String getAppearance() {
        return appearance;
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance;
//        hashMap.put("color", appearance);

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
//        hashMap.put("date", date);

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
//        hashMap.put("location", location);

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
//        hashMap.put("email", email);

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
//        hashMap.put("description", description);

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
//        hashMap.put("phone", phone);

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
//        hashMap.put("type", type);

    }

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
//        hashMap.put("found", found);

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
//        hashMap.put("key", key);

    }

    public String getNotified() {
        return notified;

    }

    public void setNotified(String notified) {
        this.notified = notified;
//        hashMap.put("notified", notified);

    }


}
