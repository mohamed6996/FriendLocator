package com.app.friendslocator;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.friendslocator.R;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.refactor.lib.colordialog.PromptDialog;

import static com.app.friendslocator.Constants.REQUEST_INVITE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference mDatabase;
    LatLng sydney;
    String lastSeen;
    int batteryPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        batteryPercentage = Utility.getBatteryPercentage(this);

        String phoneNumber = getIntent().getExtras().getString("phoneNumber");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        mDatabase.child(phoneNumber).child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("lat")) {
                    try {
                        // Toast.makeText(MapsActivity.this, "has lat", Toast.LENGTH_SHORT).show();
                        String lat_str = dataSnapshot.child("lat").getValue().toString();
                        String lan_str = dataSnapshot.child("long").getValue().toString();
                        lastSeen = dataSnapshot.child("lastSeen").getValue().toString();
                        Double lat = Double.parseDouble(lat_str);
                        Double lan = Double.parseDouble(lan_str);
                        //  Toast.makeText(MapsActivity.this, "Location changed: Lat: " + lat + " Lng: " + lan, Toast.LENGTH_SHORT).show();
                        sydney = new LatLng(lat, lan);
                        loadMap();
                    } catch (Exception e) {
                    }
                } else {

                    // Toast.makeText(MapsActivity.this, "no lat", Toast.LENGTH_SHORT).show();
                    new PromptDialog(MapsActivity.this)
                            .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                            .setAnimationEnable(true)
                            .setTitleText("Warning")
                            .setContentText("This user haven`t installed the app yet ! \nInvite him to access his location ")
                            .setPositiveListener("INVITE", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                    inviteUser();
                                }
                            }).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void inviteUser() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Friends Locator")
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);

    }

    public void loadMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //  sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f));
        //  mMap.setMyLocationEnabled(true);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                //  View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.custom_info_window, null);
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.custom_info_window, null);
                TextView battery = (TextView) view.findViewById(R.id.batteryPercentage);
                TextView last = (TextView) view.findViewById(R.id.lastSeen);

                battery.setText("Battery: " + batteryPercentage + "%");
                last.setText("Last seen on: " + lastSeen);

                return view;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }

    }
}
