package com.example.tim.lostnfound;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


class DatabaseUtils {

    private static FirebaseDatabase mDatabase;
    private static DatabaseReference mReference;


    static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }

    static DatabaseReference getReference(FirebaseDatabase database) {

        if (mReference == null) {
            mReference = database.getReference("server").child("animals");

        }

        return mReference;
    }


}
