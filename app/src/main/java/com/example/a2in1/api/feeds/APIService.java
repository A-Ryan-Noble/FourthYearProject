package com.example.a2in1.api.feeds;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIService {
    // This service uses dynamic url. This is annotated with just @Url as the endpoint url.
    @GET
    public Call<ResponseBody> socialFeedItems(@Url String url,   @Query("limit") Integer limit);
}
