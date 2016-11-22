package cse4322.GoogleHMap;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 11/12/2016.
 */

public class Database extends MainActivity {


    public ArrayList<LatLng> coordinate_pairs = new ArrayList<LatLng>();

    /*
        This is the firebase connector that retrieves the store data and pushes it to the heat map overlay
     */
    public Database getFirebaseData() {

        FirebaseDatabase database = FirebaseDatabase.getInstance(); // create a new database instance.
        DatabaseReference mDatabaseTable = database.getReference("locations");  // get the node from firebase called "locations"

        Log.d("DatabaseQuery", mDatabaseTable.toString());

        mDatabaseTable.addValueEventListener(new ValueEventListener(){

            ArrayList<LatLng> pairs = new ArrayList<LatLng>();

            public void onDataChange(DataSnapshot dataSnapshot) {       // get the current snapshot of the database to return stored points

                dataSnapshot.getChildren();

                for(DataSnapshot child: dataSnapshot.getChildren()){


                        String latitude_s =  child.child("latitudeE7").getValue().toString();
                        String longitude_s = child.child("longitudeE7").getValue().toString();

                        Double latitude = Double.valueOf(latitude_s);
                        Double longitude = Double.valueOf(longitude_s);

                        // the points are in e7. Multiply by 10 million to get the tens value. i.e "10.00"
                        latitude = latitude/10000000;
                        longitude = longitude/10000000;

                        Log.d("DatabaseListener", String.valueOf(latitude) + " " + String.valueOf(longitude));

                        // add to the pair
                        pairs.add(new LatLng(latitude,longitude));
                        //Log.d("PairsInsert",String.valueOf(pairs.get(0)));
                        Log.d("DatabasePairs", String.valueOf(pairs.size()));

                }

                // create a new database pair and set the current array values
                DBCoordinates coords = new DBCoordinates(pairs);
                Database.this.coordinate_pairs = coords.getCoordinates();
                setCoordinate_pairs(coords.getCoordinates());
                Log.d("DatabasePairsNew", String.valueOf(coords.getCoordinates()));
                Log.d("databaseCoords", String.valueOf(Database.this.coordinate_pairs));

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d("ReturnDatabasePairs", String.valueOf(getCoordinate_pairs()));
        return Database.this;
    }

    // get point data
    public ArrayList<LatLng> getCoordinate_pairs() {
        return Database.this.coordinate_pairs;
    }

    // set point data
    public void setCoordinate_pairs(ArrayList<LatLng> coordinate_pairs) {
        this.coordinate_pairs = coordinate_pairs;
    }
}

