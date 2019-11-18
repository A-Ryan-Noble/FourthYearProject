package com.example.a2in1.ui.facebook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.a2in1.FacebookGraphRequest;
import com.example.a2in1.R;

import java.util.ArrayList;
import java.util.List;

public class FacebookUsersPage extends Fragment {

    private List postsList;
    private ListView list;
    private ArrayList<String> userPosts;
    //  GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
    //                    @Override
    //                    public void onCompleted(JSONObject object, GraphResponse response) {
    //                        try {
    //                            JSONArray obj = object.getJSONObject("posts").getJSONArray("data"); // This gets the posts data
    //                            for (int i = 0; i < obj.length(); i++) {
    //                                String msg = obj.getJSONObject(i).getString("message"); // This gets only the message part of the array
    //                                Log.d(tag, msg);
    //                                posts.add(msg);
    //                            }
    //                            callback.OntLoggedInUserPosts(posts);
    //                        } catch (JSONException e) {
    //                            Log.e(tag, e.toString());
    //                        }
    //                    }
    //                }
    //        );

    private String tag = "FacebookUsersPage";

    private boolean isPosts;

    ArrayAdapter<String> arrayAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_facebook_users_page, container, false);

        list = root.findViewById(R.id.postsList);


        // if no posts are
        if (!isPosts) {
            postsList = itemsPopulate(new FacebookGraphRequest().getLoggedInUserPosts());
            Toast.makeText(getContext(), "FB feed loaded", Toast.LENGTH_SHORT).show();

            ArrayList<String> userPosts = new ArrayList<>(postsList.size());
            Log.d("zzz", "fb feed loaded");


            //isPosts = true; // stops this from being constantly called after already being called

            /* //             Reloads fragment
            Fragment fragment = getFragmentManager().findFragmentById(R.id.nav_FbContentButton);
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(fragment).attach(fragment).commit();
*/

            if (userPosts.size() == 0) {
                List<String> temp = new ArrayList<>();
                temp.add("List not yet updated with any of your posts");

                arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, temp);
                arrayAdapter.notifyDataSetChanged();

                list.invalidateViews();
                list.setAdapter(arrayAdapter);
            }

        } else {
            arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, itemsPopulate(userPosts));  // passes List to ArrayAdapter
            arrayAdapter.notifyDataSetChanged();

            list.invalidateViews();
            list.setAdapter(arrayAdapter);

            Toast.makeText(getContext(), "Searching for Feed", Toast.LENGTH_SHORT).show();
        }
        return root;
    }
    //     Populates the list in the ArrayAdapter with the posts
    private ArrayList itemsPopulate(List userPosts) {
        ArrayList<String> arrayList = new ArrayList<>(userPosts.size());
        // loop to add posts to the list
        for (int i =0; i < userPosts.size();i++){
            arrayList.add(userPosts.get(i).toString());
        }
        return arrayList;
    }
}

//      postsList = itemsPopulate(posts);
//
//                ArrayList<String> userPosts = new ArrayList<>(postsList.size());
//                Log.e("zzz","fb feed loaded");
//
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, itemsPopulate(userPosts));  // passes List to ArrayAdapter
//                arrayAdapter.notifyDataSetChanged();
//                list.invalidateViews();
//                list.setAdapter(arrayAdapter);
//
//
//                Toast.makeText(getContext(),"FB feed loaded",Toast.LENGTH_SHORT).show();