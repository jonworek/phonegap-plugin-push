package com.adobe.phonegap.push;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.lang.UnsupportedOperationException;

import org.json.JSONException;
import org.json.JSONObject;

public class GeoZoneNotifier
    implements LocationListener {

    private static final String LOG_TAG = "PushPlugin_GeoZoneNotifier";

    private GCMIntentService service;
    private Location notificationLocation;
    private Context context;
    private Bundle extras;
    private double maxDistance;

    public GeoZoneNotifier(GCMIntentService service, String geoZone, Context context, Bundle extras)
        throws JSONException {

        JSONObject jsonGeoZone = new JSONObject(geoZone);

        this.service = service;
        this.context = context;
        this.extras = extras;
        this.maxDistance = jsonGeoZone.getDouble("maxDistance");

        Location notificationLocation = new Location(LocationManager.NETWORK_PROVIDER);
        notificationLocation.setLatitude(jsonGeoZone.getDouble("latitude"));
        notificationLocation.setLongitude(jsonGeoZone.getDouble("longitude"));

        this.notificationLocation = notificationLocation;
    }

    public void showIfWithinRange() {
        // TODO: check that we have already obtained permissions for getting the user's location
        // else we should just return and not show the notification

        LocationManager locationManager = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
    }

    @Override
    public void onLocationChanged(Location deviceLocation) {
        double distance = deviceLocation.distanceTo(this.notificationLocation);

        Log.d(LOG_TAG, "distance between device and notification: [" + distance + "]");

        if (distance <= this.maxDistance) {
            Log.d(LOG_TAG, "device within range to show notification");
            service.createNotification(this.context, this.extras);
        } else {
            Log.d(LOG_TAG, "device not within range to show notification");
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onProviderEnabled(String provider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle b) {
        throw new UnsupportedOperationException();
    }

}
