package com.example.android.poddle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PodcastActivity extends AppCompatActivity implements PodcastGridAdapter.PodcastItemClickedListener{

    static String URL;
    String name;
    int id;

    RecyclerView podcastGrid;
    GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String data = getIntent().getStringExtra("PodcastBundle");
        //Log.e("DATA",data);

        try {
            JSONObject object = new JSONObject(data);
            name = object.optString("name");
            id = object.optInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        URL = "https://api.listennotes.com/api/v1/search?genre_ids="+id+"&language=English&offset=0&" +
                "q="+name+"&sort_by_date=0&type=podcast";

        podcastGrid = findViewById(R.id.podcastGrid);
        mLayoutManager = new GridLayoutManager(this,3);
        podcastGrid.setLayoutManager(mLayoutManager);

        podcastNetworkCalls(URL,MainActivity.KEY);
    }

    public void podcastNetworkCalls(String URL,String key){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key",key);
        client.get(URL,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray data = response.optJSONArray("results");

                podcastGrid.setAdapter(new PodcastGridAdapter(PodcastActivity.this,data,PodcastActivity.this));
            }
        });
    }

    @Override
    public void onPodcastItemClicked(Bundle bundle) {
        Intent mine = new Intent(this,PodacastSelectedActivity.class);
        mine.putExtra("PODCAST_INTENT",bundle.getString("PODCAST_ITEM"));
        //Log.e("PODCAST_INTENT",bundle.getString("PODCAST_ITEM"));
        startActivity(mine);
    }
}
