package com.example.a2in1.api;

import retrofit2.Retrofit;

public class APIClient {

    public static Retrofit getClient(final String base) {
        return new Retrofit.Builder().baseUrl(base).build();
       }

}