package com.tappoman.venuesearch.presenter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tappoman.venuesearch.R;
import com.tappoman.venuesearch.ui.VenueViewHolder;

public class VenuesRecyclerAdapter extends RecyclerView.Adapter<VenueViewHolder> {

    private final MainActivityPresenter presenter;

    public VenuesRecyclerAdapter(MainActivityPresenter mainActivityPresenter) {
        this.presenter = mainActivityPresenter;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VenueViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {
        presenter.onBindVenueRowViewAtPosition(position, holder);

    }

    @Override
    public int getItemCount() {
        return presenter.getRepositoriesRowsCount();
    }
}