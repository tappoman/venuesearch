package com.tappoman.venuesearch.presenter;

import android.util.Log;

import com.tappoman.venuesearch.model.Venue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivityPresenter {

    private final View view;
    private List<Venue> venueList;

    public MainActivityPresenter(View view) {
        this.view = view;
        this.venueList = new ArrayList<>();
    }

    public void onBindVenueRowViewAtPosition(int position, VenueRowView rowView) {
        Venue venue = venueList.get(position);
        rowView.setName(venue.getName());
        rowView.setAddress(venue.getAddress());
        rowView.setDistance(venue.getDistance());
    }

    public int getRepositoriesRowsCount() {
        return venueList.size();
    }

    public interface View {
        void updateVenuesRecyclerView();
    }

    public void setParsedVenueList(List<Venue> venueList) {
        this.venueList = venueList;
        view.updateVenuesRecyclerView();
    }

    public void parseVenuesJSONWithLocation(List<JSONObject> rawVenuesList, JSONObject searchLocationGeocode) {
        venueList.clear();
        for (JSONObject jsonObject : rawVenuesList) {
            Venue venue = new Venue();
            String address;
            try {
                venue.setName(jsonObject.getString("name"));
                JSONObject jsonObjectAddress = jsonObject.getJSONObject("location");
                try {
                    address = jsonObjectAddress.getString("address");
                } catch (Exception e) {
                    address = "Ei annettua osoitetta";
                }
                venue.setAddress(address);
                venue.setDistance(countDistanceToVenue(searchLocationGeocode, jsonObjectAddress));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            venueList.add(venue);
        }
        setParsedVenueList(venueList);

    }

    public void parseVenuesJSONWithCoordinates(List<JSONObject> rawVenuesList) {
        venueList.clear();

        for (JSONObject jsonObject : rawVenuesList) {
            Venue venue = new Venue();
            String address;
            try {
                venue.setName(jsonObject.getString("name"));
                JSONObject jsonObjectAddress = jsonObject.getJSONObject("location");
                try {
                    address = jsonObjectAddress.getString("address");
                } catch (Exception e) {
                    address = "Ei annettua osoitetta";
                }
                venue.setAddress(address);
                venue.setDistance(jsonObjectAddress.getInt("distance"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            venueList.add(venue);
        }
        setParsedVenueList(venueList);
    }

    private int countDistanceToVenue(JSONObject searchLocationGeocode, JSONObject destinationVenue) {

        double searchLocationCenterLatitude = 0;
        double searchLocationCenterLongitude = 0;

        try {
            JSONObject jsonObjectGeocodeCenter = searchLocationGeocode.getJSONObject("feature").getJSONObject("geometry").getJSONObject("center");
            searchLocationCenterLatitude = jsonObjectGeocodeCenter.getDouble("lat");
            searchLocationCenterLongitude = jsonObjectGeocodeCenter.getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double destinationVenueLatitude = 0;
        double destinationVenueLongitude = 0;

        try {
            destinationVenueLatitude = destinationVenue.getDouble("lat");
            destinationVenueLongitude = destinationVenue.getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double theta = searchLocationCenterLongitude - destinationVenueLongitude;
        double dist = Math.sin(Math.toRadians(searchLocationCenterLatitude)) * Math.sin(Math.toRadians(destinationVenueLatitude)) + Math.cos(Math.toRadians(searchLocationCenterLatitude)) * Math.cos(Math.toRadians(destinationVenueLatitude)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist * 1000;

        return ((int) Math.round(dist));

    }
}
