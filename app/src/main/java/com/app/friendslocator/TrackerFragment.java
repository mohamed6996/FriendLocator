package com.app.friendslocator;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.friendslocator.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class TrackerFragment extends Fragment implements ListItemClickListener {
    RecyclerView recyclerView;
    TrackAdapter adapter;
    Utility utility;
    UserData userData;
    List<ContactModel> trackersList;
    DatabaseReference mDataBase;
    TextView empty;


    public TrackerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new Utility(getContext());
        userData = new UserData(getContext());
        trackersList = new ArrayList<>();


        mDataBase = FirebaseDatabase.getInstance().getReference().child("users");
        mDataBase.child(userData.loadPhoneNumber()).child("trackers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String number = snapshot.child("number").getValue().toString();
                    String name = snapshot.child("name").getValue().toString();
                   // Log.i("CHILDREN", name + "\n" + number);
                    ContactModel model = new ContactModel(name, number);

                    if (!trackersList.contains(model)) {
                        trackersList.add(model);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.trackerRecyclerview);
        empty = (TextView) view.findViewById(R.id.trackerEmptyview);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initRecyclerview();
        checkVisibilty();
    }

    private void initRecyclerview() {
        adapter = new TrackAdapter(trackersList, getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(7));

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }

    private void checkVisibilty() {
      /*  if (trackersList == null) {
            recyclerView.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        } else {
            if (trackersList.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                empty.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.INVISIBLE);
                adapter.notifyDataSetChanged();
            }


        }
    }*/

     /* if (trackersList.isEmpty()){
          Toast.makeText(getContext(), "empty", Toast.LENGTH_SHORT).show();
          recyclerView.setVisibility(View.INVISIBLE);
          empty.setVisibility(View.VISIBLE);
          adapter.notifyDataSetChanged();
      }else {
          Toast.makeText(getContext(), "not empty", Toast.LENGTH_SHORT).show();
          recyclerView.setVisibility(View.VISIBLE);
          empty.setVisibility(View.INVISIBLE);
          adapter.notifyDataSetChanged();
      }
    }*/

        mDataBase.child(userData.loadPhoneNumber()).child("trackers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
