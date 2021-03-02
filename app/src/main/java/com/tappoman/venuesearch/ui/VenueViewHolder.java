package com.tappoman.venuesearch.ui;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tappoman.venuesearch.R;
import com.tappoman.venuesearch.presenter.VenueRowView;

public class VenueViewHolder extends RecyclerView.ViewHolder implements VenueRowView {

    TextView nameTextView;
    TextView addressTextView;
    TextView distanceTextView;

    public VenueViewHolder(View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.textView_venue_name);
        addressTextView = itemView.findViewById(R.id.textView_venue_address);
        distanceTextView = itemView.findViewById(R.id.textView_venue_distance);
    }

    @Override
    public void setName(String name) {
        nameTextView.setText(name);
    }

    @Override
    public void setAddress(String address) {
        addressTextView.setText(address);

    }

    @Override
    public void setDistance(int distance) {
        distanceTextView.setText(String.valueOf(distance));

    }
}