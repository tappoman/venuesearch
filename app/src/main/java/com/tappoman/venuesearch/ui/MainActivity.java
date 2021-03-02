package com.tappoman.venuesearch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tappoman.venuesearch.DataIds;
import com.tappoman.venuesearch.R;
import com.tappoman.venuesearch.presenter.VenuesRecyclerAdapter;
import com.tappoman.venuesearch.location.LocationInterface;
import com.tappoman.venuesearch.location.LocationListenerReceiver;
import com.tappoman.venuesearch.network.RestClient;
import com.tappoman.venuesearch.network.RestInterface;
import com.tappoman.venuesearch.presenter.MainActivityPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements LocationInterface, MainActivityPresenter.View {

    RestInterface restInterface;
    List<JSONObject> venuesList;
    double lastKnownLatitude;
    double lastKnownLongitude;
    FloatingActionButton currentLocationButton;
    EditText locationText;
    String formattedDate;
    MainActivityPresenter presenter;

    RecyclerView venuesRecyclerView;
    VenuesRecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainActivityPresenter(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!locationGranted()) {
            Intent permissionIntent = new Intent(MainActivity.this, PermissionActivity.class);
            startActivity(permissionIntent);
        }
    }

    private void init() {
        new LocationListenerReceiver(this);
        RestClient restClient = new RestClient();
        restInterface = restClient.getRestApi();
        setDateData();
        setButtonInteractions();
        setRecyclerView();
    }

    private void setRecyclerView() {
        venuesRecyclerView = findViewById(R.id.recylcerview_search_results);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        venuesRecyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new VenuesRecyclerAdapter(presenter);
        venuesRecyclerView.setAdapter(recyclerAdapter);
    }

    private void setButtonInteractions() {

        currentLocationButton = findViewById(R.id.fab_current_location);
        currentLocationButton.setOnClickListener(v -> searchByCoordinates(lastKnownLatitude, lastKnownLongitude));

        locationText = findViewById(R.id.edittext_location_search);
        locationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchByLocationName(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setDateData() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        formattedDate = df.format(c);
    }

    private void searchByLocationName(String searchString) {

        venuesList = new ArrayList<>();
        restInterface.getVenuesByLocation(DataIds.clientID, DataIds.clientSecret, formattedDate, searchString)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject json = new JSONObject(responseBody.string());
                            JSONArray jsonArray = json.getJSONObject("response")
                                    .getJSONArray("venues");
                            JSONObject geocode = json.getJSONObject("response").getJSONObject("geocode");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                venuesList.add(jsonArray.getJSONObject(i));
                            }
                            presenter.parseVenuesJSONWithLocation(venuesList, geocode);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void searchByCoordinates(double latitude, double longitude) {

        String coordinatesString = latitude + "," + longitude;
        if (latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(this, "Haetaan gps signaalia, suorita haku myöhemmin uudestaan", Toast.LENGTH_LONG).show();
            return;
        }
        venuesList = new ArrayList<>();

        restInterface.getVenuesByCoordinates(DataIds.clientID, DataIds.clientSecret, formattedDate, coordinatesString)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject json = new JSONObject(responseBody.string());
                            JSONArray jsonArray = json.getJSONObject("response")
                                    .getJSONArray("venues");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                venuesList.add(jsonObject);
                            }
                            presenter.parseVenuesJSONWithCoordinates(venuesList);

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private boolean locationGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void locationStatus(String status) {
        if (status.equals("provider disabled")) {
            Intent permissionIntent = new Intent(MainActivity.this, PermissionActivity.class);
            startActivity(permissionIntent);
        }
    }

    @Override
    public void longitude(double longitude) {
        lastKnownLongitude = longitude;
        Toast.makeText(this, "longitude " + longitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void latitude(double latitude) {
        lastKnownLatitude = latitude;
        Toast.makeText(this, "latitude " + latitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateVenuesRecyclerView() {
        this.runOnUiThread(() -> {
            recyclerAdapter.notifyDataSetChanged();
            venuesRecyclerView.scrollToPosition(0);
        });
    }
}