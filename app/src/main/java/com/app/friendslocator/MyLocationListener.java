package com.app.friendslocator;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class MyLocationListener implements LocationListener {
    Location myLocation;
    LocationManager lm;
    Context context;

    public MyLocationListener(Context context) {
        this.context = context;
        myLocation = new Location("me");
        lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {

        myLocation = location;

        Log.i("LOCATION", location.getLatitude() + " " + location.getLongitude());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
