package com.example.a2in1.api;

import com.example.a2in1.models.Post;
import com.twitter.sdk.android.core.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {
    // This service uses dynamic url. This is annotated with just @Url as the endpoint url.
    @GET
    Call<ResponseBody> socialFeedItems(@Url String url,@Query("limit") Integer limit);

    //This gets the current logged in users tweets
    @GET
    Call<ResponseBody> show(@Url String url,@Query("user_id") long userId,@Query("count")Integer count);

    // This gets the current twitter user
    @GET("/1.1/users/show.json")
    Call<User>getUserDetails();

    // This posts a simple text tweet to twitter
//    @POST("/1.1/statuses/update.json")
    @POST("/1.1/statuses/update.json")
    Call<ResponseBody> postMsgToTwitter(@Field("status")String msg);

    // This posts a text tweet and picture to twitter
    @POST("/1.1/statuses/update.json")
    Call<Post> postMsgImageToTwitter(@Field("status")String msg,@Field("media")String image);
}
//    private String userPosts;
//    private String imageUrl;
//    private String msgTags;
//    private String linkUrl;
