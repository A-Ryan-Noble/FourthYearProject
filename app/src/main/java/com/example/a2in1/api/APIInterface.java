package com.example.a2in1.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {

    // This is used tp gets the facebook user's feed
    @GET
    Call<ResponseBody> socialFeedItems(@Url String url, @Query("limit") Integer limit);

    //This is used to get the current logged in users tweets and their home timeline(Their tweets and those of who they follow)
    @GET
    Call<ResponseBody> getTweetsOfUser(@Url String url, @Query("user_id") long userId, @Query("count")Integer count);

    @GET
    Call<ResponseBody> getTweets(@Url String url, @Query("count")Integer count);
}