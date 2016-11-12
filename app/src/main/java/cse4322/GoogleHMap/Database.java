package cse4322.GoogleHMap;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by daniel on 11/12/2016.
 */

public class Database {

    public void dbRef() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("locations");

        DatabaseReference tableChild = mDatabase.child("0");

        Log.d("Database", tableChild.toString());


    }
}
