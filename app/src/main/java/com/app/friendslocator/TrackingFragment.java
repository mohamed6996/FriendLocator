package com.app.friendslocator;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.android.friendslocator.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class TrackingFragment extends Fragment implements ListItemClickListener {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    TrackAdapter adapter;
    Utility utility;
    UserData userData;
    DatabaseReference mDataBase;
    List<ContactModel> trackingList;
    List<ContactModel> trackersList;
    TextView empty;


    public TrackingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new Utility(getContext());
        userData = new UserData(getContext());
        trackingList = new ArrayList<>();
        trackersList = new ArrayList<>();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("users");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_tracking);
        recyclerView = (RecyclerView) view.findViewById(R.id.trackingRecyclerview);
        empty = (TextView) view.findViewById(R.id.trackingEmptyview);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTrackingList();
        initRecyclerview();

        checkVisibilty();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getContext() , "hello", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!utility.checkPermission()) {
                        utility.requestPermission();
                    } else {
                        pickContacts();
                    }

                } else {
                    pickContacts();
                }
            }
        });
    }

    private void initTrackingList() {
        String trackingListJson = userData.getTrackingList();
        if (!trackingListJson.equals("empty")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ContactModel>>() {
            }.getType();

            trackingList = gson.fromJson(trackingListJson, type);
        }
    }

    private void initRecyclerview() {
        adapter = new TrackAdapter(trackingList, getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(7));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = (int) viewHolder.itemView.getTag();
                Toast.makeText(getContext(), " " + position, Toast.LENGTH_SHORT).show();
                removeFromFirebase(position);
                trackingList.remove(position);
                Gson gson = new Gson();
                String trackingList_str = gson.toJson(trackingList);
                userData.saveTrackingList(trackingList_str);
                adapter.notifyDataSetChanged();

                checkVisibilty();
            }
        })

                .attachToRecyclerView(recyclerView);
    }

    private void removeFromFirebase(int position) {
        ContactModel contactModel = trackingList.get(position);
        String phoneNumber = contactModel.getContactNumber();
        mDataBase.child(userData.loadPhoneNumber()).child("tracking").child(phoneNumber).removeValue();

        mDataBase.child(phoneNumber).child("trackers").child(userData.loadPhoneNumber()).removeValue();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PICK_CONACT_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                //   Toast.makeText(MyTrackers.this,"ok",Toast.LENGTH_SHORT).show();
                Uri contactUri = data.getData();
                Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    // cuz there is only one element
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    //      Toast.makeText(MyTrackers.this,"id = "+ id,Toast.LENGTH_SHORT).show();
                    String hasPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    //  Log.i(TAG, "id =  " + id + " has phone = " + hasPhoneNumber);

                    if (hasPhoneNumber.equals("1")) {
                        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                                null,
                                null);

                        //  Log.i(TAG, "count " + phones.getCount());

                        phones.moveToFirst();
                        //  Log.i(TAG, "count " + phones.getCount());

                        String phoneNumber = phones.getString(phones.getColumnIndex("data1"));
                        //  Log.i(TAG, "phoneNumber " + phoneNumber);
                        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);

                        String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        //  Log.i(TAG, "name " + name);
                      //  Toast.makeText(getContext(), name + formattedPhoneNumber, Toast.LENGTH_SHORT).show();


                        DatabaseReference databaseReference = mDataBase.child(userData.loadPhoneNumber()).child("tracking").child(formattedPhoneNumber);
                        databaseReference.child("name").setValue(name);
                        databaseReference.child("number").setValue(formattedPhoneNumber);

                        DatabaseReference reference = mDataBase.child(formattedPhoneNumber).child("trackers").child(userData.loadPhoneNumber());
                        reference.child("name").setValue(userData.loadUserName());
                        reference.child("number").setValue(userData.loadPhoneNumber());

                        trackingList.add(new ContactModel(name, formattedPhoneNumber));
                        adapter.notifyDataSetChanged();

                        checkVisibilty();

                        // save to device memory
                        Gson gson = new Gson();
                        String trackingList_str = gson.toJson(trackingList);
                        userData.saveTrackingList(trackingList_str);
//                    *********************************************************         //
                        //     trackersList.add(new ContactModel(name, phoneNumber));
                        //     String trackersList_str = gson.toJson(trackersList);
                        //     userData.saveTrackerList(trackersList_str);

                    }

                }

            } else {
                Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    pickContacts();
                    return;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "You must accept the permission", Toast.LENGTH_LONG).show();
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

    private void pickContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, Constants.PICK_CONACT_CODE);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (utility.isNetworkAvailable()) {
            ContactModel model = trackingList.get(clickedItemIndex);
            String number = model.getContactNumber();

            // mDataBase.child(number).child("request").setValue("" + userData.getCurrentDate());  // update request node of people you track to get the last location, see MyService
            mDataBase.child(number).child("location").child("lastSeen").setValue("" + userData.getCurrentDate());

            Intent intent = new Intent(getContext(), MapsActivity.class);
            intent.putExtra("phoneNumber", number);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Check your network connection", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatPhoneNumber(String number) {
        String formattedNumber = number.replaceAll("\\s+", "");
        return formattedNumber;
    }

    private void checkVisibilty() {
        if (trackingList.size() == 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}
