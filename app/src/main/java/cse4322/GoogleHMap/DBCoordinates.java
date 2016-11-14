package cse4322.GoogleHMap;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class DBCoordinates extends MainActivity{

    ArrayList<LatLng> coordinates;

    public DBCoordinates(ArrayList<LatLng> coordinates){
        this.coordinates = coordinates;

    }

    public ArrayList<LatLng> getCoordinates() {
        return coordinates;
    }

}
