package com.app.friendslocator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.friendslocator.R;

import java.util.ArrayList;
import java.util.List;


public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackersViewHolder> {
    List<ContactModel> trackersList = new ArrayList<>();
    ListItemClickListener listener;
    Context context;

    public TrackAdapter(List<ContactModel> trackersList, Context context, ListItemClickListener listener) {
        this.trackersList = trackersList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public TrackersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new TrackersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackersViewHolder holder, int position) {
        ContactModel tracker = trackersList.get(position);
        holder.contactName.setText(tracker.getContactName());
        holder.contactNumber.setText(tracker.getContactNumber());

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return trackersList.size();
    }

    class TrackersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView contactName, contactNumber;
        ImageView locationIcon;

        public TrackersViewHolder(View itemView) {
            super(itemView);

            contactName = (TextView) itemView.findViewById(R.id.contactName);
            contactNumber = (TextView) itemView.findViewById(R.id.contactNumber);
            locationIcon = (ImageView) itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
              listener.onListItemClick(getAdapterPosition());
        }
    }
}
