package com.doodeec.eroaddemo.client;

import com.doodeec.eroaddemo.client.response.TimezoneResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * @author Dusan Bartos
 */
public interface RestService {
    @GET("/json")
    void getTimezone(@Query("location") String location,
                     @Query("key") String key,
                     Callback<TimezoneResponse> callback);
}
