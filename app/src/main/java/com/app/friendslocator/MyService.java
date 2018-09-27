package com.app.friendslocator;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MyService extends Service {
    DatabaseReference mDataBase;
    MyLocationListener myLocationListener;
    LocationManager locationManager;
    Location location;
    UserData userData;
    Utility utility;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Notification notification = new NotificationCompat.Builder(this, "friends_location")
                .setContentTitle("")
                .setContentText("").build();

        startForeground(1, notification);

        mDataBase = FirebaseDatabase.getInstance().getReference().child("users");
        userData = new UserData(this);
        utility = new Utility(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        myLocationListener = new MyLocationListener(MyService.this);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!utility.checkLocationPermission()) {
                utility.requestLocationPermission();
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
            }

        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
        }

        //  getLastKnownLocation();


        mDataBase.child(userData.loadPhoneNumber()).child("request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location = myLocationListener.myLocation;
                //   Toast.makeText(MainActivity.this, "Location changed: Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();                Log.i("LOCATION",location.getLatitude() + " " + location.getLongitude());
                // getLastKnownLocation();

                if (location == null) {
                    return;
                } else {
                    // send location to the server
                    mDataBase.child(userData.loadPhoneNumber()).child("location").child("lat").setValue(location.getLatitude());
                    mDataBase.child(userData.loadPhoneNumber()).child("location").child("long").setValue(location.getLongitude());
                    mDataBase.child(userData.loadPhoneNumber()).child("location").child("lastSeen").setValue("" + userData.getCurrentDate());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return Service.START_NOT_STICKY;
    }


}
