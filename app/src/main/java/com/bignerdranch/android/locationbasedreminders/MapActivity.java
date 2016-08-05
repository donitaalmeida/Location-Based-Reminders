package com.bignerdranch.android.locationbasedreminders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by donita on 30-07-2016.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener{

    private LocationManager locManager;
    private GoogleMap mGoogleMap;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView mEmail;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 125;

    private static final String GOOGLE_API_KEY = "AIzaSyCPlTx3VCnAHgCmmUPidLL7_Jfu4ntJiqE";
    private int PROXIMITY_RADIUS = 2400;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private Snackbar snackbarGpsNetwork = null;
    private PlaceAutocompleteFragment autocompleteFragment;
    private Place searchedPlace;
    private LinearLayout mLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mLinearLayout=(LinearLayout) findViewById(R.id.mylay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //handling absent GPS sensor error
        if(!checkGPSSensorPresent()){
            Toast.makeText(getApplicationContext(), "Cannot run without gps sensor", Toast.LENGTH_SHORT).show();
        }else{
            //handling GooglePlayservices framework absent error
            if(isGooglePlayServicesAvailable()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    //if all ok start app
                    startApp();
                }
            }
        }

    }

    private boolean checkGPSSensorPresent(){
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    //method to check if google place service is available
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void initiate(){
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();

        autocompleteFragment.setFilter(typeFilter);
        //attach autocomplete listner
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                searchedPlace =place;
                    Marker myplace=setMarker(place.getLatLng(),place.getAddress().toString());
                    myplace.showInfoWindow();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });
        updateLocation();
        //TODO arrow function
    }
    private void startApp() {
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //initailize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    public Marker setMarker(LatLng current, String address){
        mGoogleMap.setOnMarkerClickListener(this);
        float zoomLevel = 14.0f;
        mGoogleMap.clear();
        Marker marker= mGoogleMap.addMarker(new MarkerOptions().position(current).title(address));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,zoomLevel));
        return marker;

    }

    //update location to current location
    public void updateLocation() {
        showGpsNetworkSnackbar();
        try {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation == null) {
                mLocation = new Location("SJSU");
                mLocation.setLatitude(37.3351895);
                mLocation.setLongitude(-121.8821658);
                setMarker(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), "SJSU(Default)");
            } else {
                Log.d("curlat",mLocation.getLatitude()+"");
                Log.d("curlng",mLocation.getLongitude()+"");
                setMarker(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), "You are here");
            }
            autocompleteFragment.setBoundsBias(setBounds(mLocation,3000));
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    //when permission are granted/denied this method is called
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startApp();
                } else {

                    Snackbar.make(toolbar, "Location access is required to show coffee shops nearby.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                                    }
                                }
                            }).setActionTextColor(Color.RED).show();
                }
                return;
            }

        }
    }

    // method to set boundry for the searchbar suggestions
    private LatLngBounds setBounds(Location location, int mDistanceInMeters ){
        double latRadian = Math.toRadians(location.getLatitude());
        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;
        double minLat = location.getLatitude() - deltaLat;
        double minLong = location.getLongitude() - deltaLong;
        double maxLat = location.getLatitude() + deltaLat;
        double maxLong = location.getLongitude() + deltaLong;
        return  new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat, maxLong));
    }
    //check if GPS and Netork are enabled and promt user to enable the same if required
    private void showGpsNetworkSnackbar() {

        boolean flag = false;
        StringBuilder msg = new StringBuilder();
        if(!isGpsEnabled()){
            flag = true;
            msg.append("Please Enable GPS");
        }
        if(!isNetworkEnabled()){
            if (flag) {
                msg.append(" & Internet");
            } else {
                flag = true;
                msg.append("Please Enable Internet");
            }
        }
        if (flag) {

            snackbarGpsNetwork = Snackbar.make(mLinearLayout, msg.toString(), Snackbar.LENGTH_INDEFINITE);
            if(!isNetworkEnabled()){
                snackbarGpsNetwork.setAction("Enable Internet", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                });
            }
            if(!isGpsEnabled()){
                snackbarGpsNetwork.setAction("Enable GPS", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);

                    }
                });
            }
            snackbarGpsNetwork.show();
        } else {
            if (snackbarGpsNetwork != null) {
                snackbarGpsNetwork.dismiss();
                recreate();
            }
        }

    }
    //check if gps is enabled
    private boolean isGpsEnabled() {
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locManager != null) {
            return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            return false;
        }
    }
    // check if network is enabled
    private boolean isNetworkEnabled() {
        NetworkConnectionDetector networkConnectionDetector=new NetworkConnectionDetector(getApplicationContext());
        if (networkConnectionDetector != null) {
            return networkConnectionDetector.isConnectingToInternet();
        } else {
            return false;
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initiate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        }
        mGoogleApiClient.connect();
    }
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(searchedPlace!=null){
            Intent intent=new Intent(getApplicationContext(),AddReminder.class);
            intent.putExtra("lat",searchedPlace.getLatLng().latitude);
            intent.putExtra("lng",searchedPlace.getLatLng().longitude);
            intent.putExtra("placeName",searchedPlace.getName().toString());
            intent.putExtra("placeAddress",searchedPlace.getAddress().toString());
            intent.putExtra("placeType",searchedPlace.getPlaceTypes().get(0));
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"Select a place",Toast.LENGTH_LONG).show();
        }

        return true;
    }
}
