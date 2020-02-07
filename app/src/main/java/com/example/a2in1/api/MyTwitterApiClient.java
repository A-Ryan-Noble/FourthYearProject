package com.example.a2in1.api;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

public class MyTwitterApiClient extends TwitterApiClient {
        public MyTwitterApiClient(TwitterSession session){
            super(session);
        }

        public APIInterface getApiInterface() {
            return getService(APIInterface.class);
        }
}
