package com.example.tim.lostnfound;

import com.google.firebase.database.FirebaseDatabase;


class DatabaseUtils {

    private static FirebaseDatabase mDatabase;


    static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }


}
