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
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements GenreGridAdapter.GridItemClickListener{

    GridLayoutManager mLayoutManager;
    RecyclerView genreView;
    static String KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        genreView = (RecyclerView) findViewById(R.id.genreGrid);
        mLayoutManager = new GridLayoutManager(this,3);
        genreView.setLayoutManager(mLayoutManager);

        networkCalls("https://api.listennotes.com/api/v1/genres",KEY);

    }
    public void networkCalls(String URL,String key){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key",key);
        client.get(URL,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray data = response.optJSONArray("genres");

                genreView.setAdapter(new GenreGridAdapter(MainActivity.this,data,MainActivity.this));
            }
        });
    }


    @Override
    public void onGridItemClickListener(Bundle bundle) {
        Intent mine = new Intent(this,PodcastActivity.class);
        mine.putExtra("PodcastBundle",bundle.getString("GENRE_OBJECT"));
        startActivity(mine);
    }
}
