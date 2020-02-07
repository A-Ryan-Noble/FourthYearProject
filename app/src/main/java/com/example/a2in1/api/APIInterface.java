package com.example.a2in1.api;

import com.twitter.sdk.android.core.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {
    // This service uses dynamic url. This is annotated with just @Url as the endpoint url.
    @GET
    Call<ResponseBody> socialFeedItems(@Url String url,@Query("limit") Integer limit);

    //This gets the current logged in users tweets
//    @GET("/1.1/users/show.json")
    @GET
    Call<ResponseBody> show(@Url String url,@Query("user_id") long userId,@Query("count")Integer count);

}
