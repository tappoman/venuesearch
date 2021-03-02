package com.tappoman.venuesearch.model;

import com.squareup.moshi.Json;

import org.parceler.Parcel;

@Parcel(value = Parcel.Serialization.BEAN)
public class Venue {

    @Json(name = "name")
    private String name;

    @Json(name = "address")
    private String address;

    @Json(name = "distance")
    private int distance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

}
