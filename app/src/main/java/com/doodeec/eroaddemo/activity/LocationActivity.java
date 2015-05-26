package com.doodeec.eroaddemo.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.doodeec.eroaddemo.R;
import com.doodeec.eroaddemo.adapter.CustomInfoWindowAdapter;
import com.doodeec.eroaddemo.client.RestClient;
import com.doodeec.eroaddemo.client.response.TimezoneResponse;
import com.doodeec.eroaddemo.utils.DateUtils;
import com.doodeec.eroaddemo.utils.ERLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        Callback<TimezoneResponse> {

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Location request
     */
    private LocationRequest mLocationRequest;

    /**
     * Current/last known location
     */
    private Location mCurrentLocation;

    /**
     * Map instance
     */
    private GoogleMap mGoogleMap;

    /**
     * Current position marker
     */
    private Marker mCurrentPositionMarker;

    /**
     * Timezone name
     */
    private String mTimezoneName;

    /**
     * Map info window
     */
    private CustomInfoWindowAdapter mCustomInfoAdapter;

    /**
     * Flag to determine if map is loaded
     */
    private boolean mMapReadyFlag = false;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setNumUpdates(1)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Start connection to Google play services
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        ERLog.i("GoogleApiClient connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            onLocationChanged(location);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        ERLog.i("GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @SuppressWarnings("Convert2Lambda")
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        ERLog.i("GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }
        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            ERLog.e("Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        ERLog.i("Location loaded", location.toString());
        mCurrentLocation = location;
        if (mMapReadyFlag) {
            showLocation();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapReadyFlag = true;
        mGoogleMap = googleMap;
        if (mCurrentLocation != null) {
            showLocation();
        }
    }

    @Override
    public void success(TimezoneResponse timezoneResponse, Response response) {
        ERLog.i("Timezone loaded", response.toString());
        if (timezoneResponse.getTimeZoneId() != null) {
            mTimezoneName = timezoneResponse.getTimeZoneId();
        } else {
            mTimezoneName = null;
        }
        updateMarkerSnippet();
    }

    @Override
    public void failure(RetrofitError error) {
        ERLog.e("Error loading timezone", error);
    }

    private void showLocation() {
        LatLng myLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));

        // if marker doesn't exist, create one
        if (mCurrentPositionMarker == null) {
            mCustomInfoAdapter = new CustomInfoWindowAdapter(this);
            mGoogleMap.setInfoWindowAdapter(mCustomInfoAdapter);
            mCurrentPositionMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(myLocation));
        }

        updateMarkerSnippet();
        mCurrentPositionMarker.setTitle(getString(R.string.location));
        mCurrentPositionMarker.setPosition(myLocation);
        // show info window if it is not shown
        mCurrentPositionMarker.showInfoWindow();

        //send Timezone API request
        String location = String.format(Locale.ENGLISH, "%.2f,%.2f", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        String apiKey = getString(R.string.timezone_api_key);
        if (location != null && apiKey != null) {
            RestClient.get().getTimezone(location, apiKey, System.currentTimeMillis() / 1000, this);
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    private void updateMarkerSnippet() {
        mCustomInfoAdapter.update(
                String.format(Locale.ENGLISH, "%s %.4f", getString(R.string.latitude), mCurrentLocation.getLatitude()),
                String.format(Locale.ENGLISH, "%s %.4f", getString(R.string.longitude), mCurrentLocation.getLongitude()),
                String.format("%s %s", getString(R.string.local_time), DateUtils.getLocalTime()),
                String.format("%s %s", getString(R.string.utc_time), DateUtils.getUTCtime()),
                String.format("%s %s", getString(R.string.timezone), mTimezoneName));
        mGoogleMap.setInfoWindowAdapter(mCustomInfoAdapter);
    }
}
