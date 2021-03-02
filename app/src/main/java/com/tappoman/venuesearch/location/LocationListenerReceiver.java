package com.tappoman.venuesearch.location;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

public class LocationListenerReceiver implements LocationListener {

    private final LocationInterface locationInterface;
    private final Context context;

    public LocationListenerReceiver(Context context){
        this.context = context;
        this.locationInterface = (LocationInterface) context;
        initLocationListener();
    }

    private void initLocationListener(){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationInterface.latitude(location.getLatitude());
        locationInterface.longitude(location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        locationInterface.locationStatus("provider disabled");
    }

}
