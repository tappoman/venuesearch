package com.tappoman.venuesearch.ui;

import android.Manifest;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.tappoman.venuesearch.R;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class PermissionActivity extends AppCompatActivity {

    private static final int location_REQUEST_CODE = 100;
    private boolean locationEnabled;
    private boolean gpsEnabled;
    private Button gpsButton;
    private Button locationButton;
    private Button continueButton;
    private LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layout_permissions);
        gpsButton = findViewById(R.id.permission_button_enable_gps);
        gpsButton.setOnClickListener(v -> gpsOn());
        locationButton = findViewById(R.id.permission_button_enable_location);
        locationButton.setOnClickListener(v -> requestLocation());
        continueButton = findViewById(R.id.permission_button_continue);
        continueButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(PermissionActivity.this, MainActivity.class);
            startActivity(mainIntent);
        });
        checkButtonStates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsEnabled = true;
                gpsButton.setVisibility(VISIBLE);
                setButtonEnabled(gpsButton, getString(R.string.permission_button_gps_active));
            } else {
                gpsEnabled = false;
                gpsButton.setVisibility(VISIBLE);
                setButtonDisabled(gpsButton, getString(R.string.permission_button_gps));
            }
        }
        int locationPermission = ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            setButtonEnabled(locationButton, getString(R.string.permission_button_location_active));
            locationEnabled = true;
            gpsButton.setVisibility(VISIBLE);
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsEnabled = true;
                setButtonEnabled(gpsButton, getString(R.string.permission_button_gps_active));
            } else {
                setButtonDisabled(gpsButton, getString(R.string.permission_button_gps));
            }
        }
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                setButtonEnabled(gpsButton, getString(R.string.permission_button_gps_active));
                gpsEnabled = true;
            }
        }
        checkButtonStates();
    }

    private void checkPermission() {


        if (gpsEnabled && locationEnabled) {
            continueButton.setBackgroundColor(Color.parseColor("#0aa533"));
            continueButton.setAlpha(1f);
            continueButton.setClickable(true);
            continueButton.setVisibility(VISIBLE);
        } else {
            continueButton.setVisibility(GONE);
        }
    }

    private void requestLocation() {

        int locationPermission = ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    location_REQUEST_CODE);
            return;
        }
        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            locationEnabled = true;
            setButtonEnabled(locationButton, getString(R.string.permission_button_location_active));
            checkButtonStates();
            checkPermission();
        }

    }

    private void gpsOn() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
        }
    }

    private void setButtonEnabled(Button button, String text) {
        String check = "\u2713";
        button.setAlpha(0.5f);
        button.setClickable(false);
        button.setText(String.format("%s  %s", text, check));
    }

    private void setButtonDisabled(Button button, String text) {
        button.setAlpha(1f);
        button.setClickable(true);
        button.setText(text);
    }

    private void checkButtonStates() {
        if (!gpsEnabled) {
            setButtonDisabled(gpsButton, getString(R.string.permission_button_gps));
        } else {
            setButtonEnabled(gpsButton, getString(R.string.permission_button_gps_active));
        }
        if (!locationEnabled) {
            setButtonDisabled(locationButton, getString(R.string.permission_button_location));
            gpsButton.setVisibility(INVISIBLE);
        } else {
            setButtonEnabled(locationButton, getString(R.string.permission_button_location_active));
            gpsButton.setVisibility(VISIBLE);
        }
        if (gpsEnabled && locationEnabled) {
            checkPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == location_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationEnabled = true;
                setButtonEnabled(locationButton, getString(R.string.permission_button_location_active));
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locationManager != null) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        gpsEnabled = true;
                        gpsButton.setVisibility(VISIBLE);
                        setButtonEnabled(gpsButton, getString(R.string.permission_button_gps_active));
                    } else {
                        gpsButton.setVisibility(VISIBLE);
                    }
                }
            }
            checkPermission();
            checkButtonStates();
        }
    }
}

