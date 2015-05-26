package com.doodeec.eroaddemo.client;

import com.doodeec.eroaddemo.ERoadConfig;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * @author Dusan Bartos
 */
public class RestClient {
    private static RestService REST_CLIENT;

    static {
        setupRestClient();
    }

    private RestClient() {
    }

    public static RestService get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ERoadConfig.API_BASE)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        REST_CLIENT = restAdapter.create(RestService.class);
    }
}
