package cse4322.GoogleHMap;

import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

// libraries for spinners
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

// libraries to handle text display
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

// google maps API libraries
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

// Location services libraries
import com.google.android.gms.location.LocationServices;

// LatLng libraries
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    // initiate Objects for GoogleMaps application
    protected GoogleMap mGmap;
    private GoogleApiClient mGoogleServicesClient;

    private Double mCurrentLat;     // stores the current latitude
    private Double mCurrentLong;    // stores the current longitude

    protected int mapType = 1; // set default for the map = NORMAL

    // initiate object for spinner
    Spinner drop_down;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*

            Ensure a fragment is created for the spinner to attach to. Then have the OnItemSelectedListener wait for input

 */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);              // display a map on the given Activity Fragment.

        initiateClient();           // setup client connector for Google services API
        generateSpinner();                      // place the spinner on the fragment view.

        drop_down.setOnItemSelectedListener(this);      // wait for the user to hit spinner drop down.
    }
    /*
        Method: InitiateClient()
        Parameters: none
        return: none
        Use: Creates a connector to the Google Services API. This is needed to retrieve location data (longitude and latitude).
     */
    private void initiateClient() {

        if (mGoogleServicesClient == null){

            mGoogleServicesClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            mGoogleServicesClient.connect();

        }
    }
    /*  API
        Method: onConnected(Bundle bundle)
        parameters: bundle
        return: none
        Use: when initiateClient successfully connects, then access the location data
     */
    public void onConnected(@Nullable Bundle bundle) {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleServicesClient);

        if(mLastLocation != null){

            this.mCurrentLat = mLastLocation.getLatitude();
            this.mCurrentLong = mLastLocation.getLongitude();

        }

    }
    /*  API
        Method: onConnectionSuspended
        parameters: int i
        return: none
        Use: API requires this method to be included. If the connection is suspended...do nothing.
     */
    public void onConnectionSuspended(int i) {

    }
    /*
        Method: onConnectedFailed(ConnectedResult connectionResult)
        parameters: connectionResult
        return: none
        Use: when initiateClient fails to connect, then display the failed result.

     */
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(MainActivity.this, "Connection Result: " + connectionResult, Toast.LENGTH_SHORT).show();

    }

    // OnMapReady comes with the google API. When a selection is made via spinner, then update OnMapReady
    /* API
        Method: OnMapReady(GoogleMap googleMap)
        parameters: googleMap
        return: none
        Use: when the map fragment is called (mapAsync()), then onMapReady will instantiate a non-null instance of
            the GoogleMaps object. All map information (adding, changing map types etc.) are managed here.
     */
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMapType(mapType);
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
        googleMap.setMyLocationEnabled(true);
        addMarkerLongClickListener(googleMap);
        generateHeatMapButton(googleMap);
        toggleHeatMap(googleMap);
    }

/*
    FunctionName: AddMarkerLongClickListener
    parameters: (NonNull) Map
    return none
    Use: Listens for a long press on the map. If it's more then 1 second, then a marker will be placed at that position.
    The user can then hold the marker, and drag the marker to a new position.
*/
    public void addMarkerLongClickListener(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.addMarker(new MarkerOptions().position(latLng).draggable(true));
            }
        });

    }
    /*
        Method: generateSpinner()
        parameters: none
        return: none
        Use: create the Spinner object that will be used to switch between map types.
     */
    private void generateSpinner() {

        drop_down = (Spinner) findViewById(R.id.maptype_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.maptype_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drop_down.setAdapter(adapter);
    }


    /*
        MethodName: CreateMap
        Parameters: Map, mapType
        Use: A pass function for "OnMapReady". Createmap will receive the mapType and current map, then
            pass those values to "OnMapReady".
    */
    private void createMap(GoogleMap map, int mapType){

        try {
            if (map == null) {      // by default, the GoogleMap object will be null, meaning the map hasn't been created yet.

                MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                onMapReady(map);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*  API
        Method: OnItemSelected
        parameters: parent, view, position, id
        return: none
        Use: when an item in the spinner is selected, then create a map that corresponds to the map type.
     */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

 /*
         check the text which was selected from the drop down.
         The spinner drop down should appear as the following...

         Normal
         Satellite
         Terrain
         Hybrid

         then if nothing else, choose "none"

*/
        TextView item = (TextView) view;

        if(item.getText().equals("Normal")){
            Toast.makeText(this, "Changing map type to "+item.getText(),Toast.LENGTH_SHORT).show();
            mapType = GoogleMap.MAP_TYPE_NORMAL;
            createMap(mGmap, mapType);

        }

        if(item.getText().equals("Satellite")){
            Toast.makeText(this, "Changing map type to "+item.getText(),Toast.LENGTH_SHORT).show();
            mapType = GoogleMap.MAP_TYPE_SATELLITE;
            createMap(mGmap, mapType);

        }

        if(item.getText().equals("Terrain")){
            Toast.makeText(this, "Changing map type to "+item.getText(),Toast.LENGTH_SHORT).show();
            mapType = GoogleMap.MAP_TYPE_TERRAIN;
            createMap(mGmap, mapType);

        }
        if(item.getText().equals("Hybrid")){
            Toast.makeText(this, "Changing map type to "+item.getText(),Toast.LENGTH_SHORT).show();
            mapType = GoogleMap.MAP_TYPE_HYBRID;
            createMap(mGmap, mapType);


        }
        if(item.getText().equals("None")){
            Toast.makeText(this, "Changing map type to "+item.getText(),Toast.LENGTH_SHORT).show();
            mapType = GoogleMap.MAP_TYPE_NONE;
            createMap(mGmap, mapType);

        }

    }
/*
    Function Name: OnNothingSelected
    parameters: AdapterView parent
    Return: none
    Use: If the user does nothing on the menu, then do nothing.
     Required for OnItemSelected method.
*/
        public void onNothingSelected(AdapterView<?> parent) {
        // if nothing selected then do nothing
    }

    private void generateHeatMapButton(final GoogleMap googleMap) {

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                addHeatMap(googleMap);

            }
        });
    }

    private void toggleHeatMap(final GoogleMap googleMap) {

        // get ToggleButton
        ToggleButton b = (ToggleButton) findViewById(R.id.toggleButton);

        // attach an OnClickListener
        b.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // removeHeatMap function when off
                // add heat map function when on
            }
        });
    }

    /**
     * Reads the locations data from a JSON file and returns an array of MarkerOptions objects
     * that can then be used to add markers to a GoogleMap object.
     *
     * TODO Citations
     * http://stackoverflow.com/questions/19945411/android-java-how-can-i-parse-a-local-json-file-from-assets-folder-into-a-listvi/19945484#19945484
     *
     * @param filePath the name of the file located in the assets folder.
     */
    private MarkerOptions[] readJSON(String filePath){
        String jsonStr = null;
        // GET JSON AS STRING
        try {
            InputStream inStream = getAssets().open("Sample_LocationHistory_small.json");
//            InputStream inputStream = new FileInputStream(filePath);
            int size = inStream.available();
            byte[] buffer = new byte[size];
            inStream.read(buffer);
            inStream.close();
            getAssets().close();
            jsonStr = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        JSONObject jsonObj;
        JSONArray locations;
        MarkerOptions[] markerOptions = null;
        // RETRIEVE JSONArray FROM FILE and GET MarkerOptions
        try {
            jsonObj = new JSONObject(jsonStr);
            locations = jsonObj.getJSONArray("locations"); // gets a giant JSONArray of all the locations in the JSON

            markerOptions = new MarkerOptions[locations.length()];
            for(int i = 0; i < locations.length(); i++) {
                jsonObj = locations.getJSONObject(i);
                long longitude = jsonObj.getLong("longitudeE7");
                long latitude = jsonObj.getLong("latitudeE7");
                Log.d("readJSON()", "["+ i + "]" + " :: longitude = " + longitude + ", latitude = " + latitude);
                markerOptions[i] = new MarkerOptions();
                markerOptions[i].position(new LatLng(latitude, longitude));
                markerOptions[i].visible(true);
                Log.d("readJSON()", "markerOptions[" + i + "].getPosition() = " + markerOptions[i].getPosition().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return markerOptions;
    }

    private void addHeatMap(GoogleMap googlemap){
        // function to add the map once the json file is parsed
    }

    private void removeHeatMap(GoogleMap googleMap){
        // add code to remove heat map function: mOverlay.remove();
    }


}
