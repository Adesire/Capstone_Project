package com.example.android.poddle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class PodcastSelectedFragment extends Fragment {

    TextView originalDescription;
    ImageView podcastImage;
    String originTitle,originDescription,imageURL,podcastID;
    RecyclerView episodesGrid;
    LinearLayoutManager mLayoutManager;
    private CollapsingToolbarLayout mToolbarLayout;
    private Toolbar mToolbar;
    int i;
    ArrayList<PodcastModel> episodes;
    EpisodesGridAdapter episodeAdapter;
    ProgressBar loading;


    public static PodcastSelectedFragment NewInstance(String podcastData){
        PodcastSelectedFragment fragment = new PodcastSelectedFragment();
        Bundle b = new Bundle();
        b.putString("PODCAST_DATA",podcastData);
        fragment.setArguments(b);
        return fragment;
    }

    private PodacastSelectedActivity getActivityCast(){return (PodacastSelectedActivity)getActivity();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_podcast_selected,container,false);
        originalDescription = v.findViewById(R.id.selected_description);
        podcastImage = v.findViewById(R.id.selected_image);
        episodesGrid = v.findViewById(R.id.episodesGrid);
        loading = v.findViewById(R.id.progressBar);

        mToolbarLayout = (CollapsingToolbarLayout)v.findViewById(R.id.collapsing_toolbar_layout);
        mToolbar = v.findViewById(R.id.app_bar);

        mLayoutManager = new LinearLayoutManager(getActivityCast());
        episodesGrid.setLayoutManager(mLayoutManager);

        episodes = new ArrayList<>();

        episodeAdapter = new EpisodesGridAdapter(getActivityCast(),episodes);
        episodesGrid.setAdapter(episodeAdapter);

        episodesGrid.setHasFixedSize(true);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle dataBundle = getArguments();
        String data = dataBundle.getString("PODCAST_DATA");
        try {
            JSONObject object = new JSONObject(data);
            originTitle = object.optString("title_original");
            originDescription = object.optString("description_original");
            podcastID = object.optString("id");
            imageURL = object.optString("image");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mToolbarLayout.setTitle(originTitle);
        originalDescription.setText(originDescription);
        Picasso.with(getActivityCast()).load(imageURL).into(podcastImage);

        String URL = "https://listennotes.p.mashape.com/api/v1/search?language=English" +
                "&ocid="+podcastID+"&offset=0&" +
                "q="+originTitle+"&sort_by_date=1&type=episode";

        selectedPodcastNetworking(URL,MainActivity.KEY);

        episodesGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    if(!episodesGrid.canScrollVertically(RecyclerView.FOCUS_DOWN)){
                        final String URL2 = "https://listennotes.p.mashape.com/api/v1/search?language=English" +
                                "&ocid="+podcastID+"&offset="+ i +"&" +
                                "q="+originTitle+"&sort_by_date=1&type=episode";
                        Log.e("TAG for i",String.valueOf(i));
                        nextPageNetworking(URL2,MainActivity.KEY);
                    }
                }
            }
        });

    }

    private void selectedPodcastNetworking(String URL,String key){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key",key);
        client.get(URL,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                loading.setVisibility(View.INVISIBLE);
                super.onSuccess(statusCode, headers, response);
                JSONArray podcastData = response.optJSONArray("results");
                i = response.optInt("next_offset");

                for(int j=0;j<podcastData.length();j++){
                    JSONObject o = podcastData.optJSONObject(j);
                    episodes.add(new PodcastModel(o.optString("title_original"),o.optString("description_original")));
                    episodeAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    private void nextPageNetworking(String URL,String key){
        loading.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key",key);
        client.get(URL,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                loading.setVisibility(View.INVISIBLE);
                JSONArray podcastDataNext = response.optJSONArray("results");
                i = response.optInt("next_offset");

                Log.e("TAG for other i",String.valueOf(i));

                for(int j=0;j<podcastDataNext.length();j++){
                    JSONObject o = podcastDataNext.optJSONObject(j);
                        episodes.add(new PodcastModel(o.optString("title_original"),o.optString("description_original")));
                        episodeAdapter.notifyDataSetChanged();

                }
            }
        });
    }

}
