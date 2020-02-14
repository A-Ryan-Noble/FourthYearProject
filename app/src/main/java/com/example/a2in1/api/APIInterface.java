package com.example.a2in1.api;

import com.twitter.sdk.android.core.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {

    /* This service uses dynamic url. This is annotated with just @Url as the endpoint url.
     Gets user's facebook feed
     */
    @GET
    Call<ResponseBody> socialFeedItems(@Url String url, @Query("limit") Integer limit);

    // This posts a message and image to facebook
    @POST
    Call<ResponseBody> postImgMessage(@Url String url);

    //This gets the current logged in users tweets
    @GET
    Call<ResponseBody> getTweetsOfUser(@Url String url, @Query("user_id") long userId, @Query("count")Integer count);

    // This gets the current twitter user
    @GET("/1.1/users/show.json")
    Call<User>getUserDetails();

    // This posts a simple text tweet to twitter
    @POST("/1.1/statuses/update.json")
    Call<ResponseBody> postMsgToTwitter(@Query("status") String msg);
}