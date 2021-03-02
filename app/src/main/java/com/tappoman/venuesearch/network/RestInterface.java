package com.tappoman.venuesearch.network;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RestInterface {

    @GET("{searchString}")
    List<String> getNearbyVenuesss(@Path("searchString") String searchString);

    @GET("search")
    @Headers("Content-Type: application/json")
    Observable<ResponseBody> getVenuesByLocation(@Query("client_id") String id, @Query("client_secret") String secret, @Query("v") String version, @Query("near") String searchQuery);

    @GET("search")
    @Headers("Content-Type: application/json")
    Observable<ResponseBody> getVenuesByCoordinates(@Query("client_id") String id, @Query("client_secret") String secret, @Query("v") String version, @Query("ll") String currentLocation);

}
