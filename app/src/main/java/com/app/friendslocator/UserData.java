package com.app.friendslocator;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserData {
    Context context;
    SharedPreferences sharedPreferences;

    public UserData(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
    }

    // when register, save phone number in  shared pref .
    public void setPhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
    }

    public String loadPhoneNumber() {
        String phoneNumber = sharedPreferences.getString("phoneNumber", "empty");
        if (phoneNumber.equals("empty")) {
            Intent intent = new Intent(new Intent(context, Login.class));
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // come from normal class
            //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // when user clicks back btn, he`ll exit the app
            context.startActivity(intent);
        }

        return phoneNumber;
    }

    // when register, save phone number in  shared pref .
    public void setUserName(String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.apply();
    }

    public String loadUserName() {
        String phoneNumber = sharedPreferences.getString("userName", "empty");
        if (phoneNumber.equals("empty")) {
            //  Intent intent = new Intent(new Intent(context, Login.class));
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // come from normal class
            //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // when user clicks back btn, he`ll exit the app
            //  context.startActivity(intent);
        }

        return phoneNumber;
    }

    public void saveTrackingList(String trackingList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("trackingList", trackingList);
        editor.apply();
    }

    public String getTrackingList() {
        String trackingList = sharedPreferences.getString("trackingList", "empty");
        return trackingList;
    }


    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
        Date date = new Date();
        String formattedDate = dateFormat.format(date).toString();
        return formattedDate;
    }


    public void saveTrackerList(String trackerList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("trackerList", trackerList);
        editor.apply();
    }

    public String getTrackerList() {
        String trackerList = sharedPreferences.getString("trackerList", "empty");
        return trackerList;
    }



}
