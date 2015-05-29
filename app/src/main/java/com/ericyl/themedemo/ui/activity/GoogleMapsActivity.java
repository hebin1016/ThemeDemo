package com.ericyl.themedemo.ui.activity;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ericyl.themedemo.R;
import com.ericyl.themedemo.ui.service.FetchAddressIntentService;
import com.ericyl.themedemo.util.AppProperties;
import com.ericyl.themedemo.util.Constants;
import com.ericyl.themedemo.util.WGS84TOGCJ02;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class GoogleMapsActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location currentLocation;

    private final String LOCATION_KEY = "CurrentLocation";

    private AddressResultReceiver resultReceiver = new AddressResultReceiver(new Handler());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        setUpMapIfNeeded();
        buildGoogleApiClient();
        createLocationRequest();
        updateValuesFromBundle(savedInstanceState);
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
//        locationRequest = new LocationRequest();
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(16);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationRequest = LocationRequest.create().setInterval(10000).setFastestInterval(16).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(LOCATION_KEY, currentLocation);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                currentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            if (googleApiClient.isConnected())
                updateUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (!googleApiClient.isConnected())
            googleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        getLastLocation();
        if (currentLocation != null)
            updateUI();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void getLastLocation() {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        updateUI();
    }

    private void updateUI() {
        LatLng latLng = WGS84TOGCJ02.transformFromWGSToGCJ(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        Toast.makeText(this, latLng.latitude + "," + latLng.longitude, Toast.LENGTH_SHORT).show();
        if (!Geocoder.isPresent()) {
            Toast.makeText(this, R.string.no_geocoder_available,
                    Toast.LENGTH_LONG).show();
            return;
        }
        startIntentService(latLng);
    }

    protected void startIntentService(LatLng latLng) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, latLng);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(AppProperties.getContext()) == ConnectionResult.SUCCESS)
            stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        googleApiClient.disconnect();
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
//            if (resultCode == Constants.SUCCESS_RESULT) {
            Toast.makeText(GoogleMapsActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
//            }

        }
    }

}