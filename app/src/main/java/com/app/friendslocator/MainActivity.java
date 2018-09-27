package com.app.friendslocator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.friendslocator.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    TabLayout tabLayout;
    List<Fragment> fragmentList;
    List<String> titleList;
    UserData userData;
    Utility utility;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        userData = new UserData(this);
        utility = new Utility(this);
        if (! userData.loadPhoneNumber().equals("empty")){
            initialize();
            initData();

            pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setLayoutDirection(ViewPager.LAYOUT_DIRECTION_LTR); // fix arabic isuue

            // access location
            accessLocation();

        }

    }

    private void initData() {
        addData(new TrackingFragment(), "Tracking");
        addData(new TrackerFragment(), "Trackers");
    }

    private void addData(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    public void initialize() {
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

    }

    private void accessLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!utility.checkLocationPermission()) {
                utility.requestLocationPermission();
            } else {
                ContextCompat.startForegroundService(this,new Intent(this, MyService.class));
             //   startService(new Intent(MainActivity.this, MyService.class));
            }

        } else {
            ContextCompat.startForegroundService(this,new Intent(this, MyService.class));
         //   startService(new Intent(MainActivity.this, MyService.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    ContextCompat.startForegroundService(this,new Intent(this, MyService.class));
                    //  startService(new Intent(MainActivity.this, MyService.class));
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "You must accept the permission", Toast.LENGTH_LONG).show();
                }
                return;
            }


        }
    }
}
