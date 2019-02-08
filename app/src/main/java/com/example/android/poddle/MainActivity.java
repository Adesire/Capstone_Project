package com.example.android.poddle;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.android.poddle.data.AppDatabase;
import com.example.android.poddle.data.FavouritePodcasts;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

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

        networkCalls("https://listennotes.p.mashape.com/api/v1/genres",KEY);



    }



    private void networkCalls(String URL,String key){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key",key);
        client.get(URL,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray data = response.optJSONArray("genres");
                Log.e("DATA",data.toString());

                genreView.setAdapter(new GenreGridAdapter(MainActivity.this,data,MainActivity.this));
            }
        });
    }

    @Override
    public void onGridItemClickListener(Bundle bundle) {
        Intent mine = new Intent(this,PodcastActivity.class);
        mine.putExtra("PodcastBundle",bundle.getString("GENRE_OBJECT"));
        PodcastActivity.x = 0;
        startActivity(mine);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView  = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Podcasts");
        //Log.e("TAAAAG",searchView.getQuery().toString());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("TAAAAG1",query);


                Intent mine = new Intent(MainActivity.this,PodcastActivity.class);
                //Log.e("TED",data.toString());
                mine.putExtra("search_results",query);
                mine.putExtra("PodcastBundle","");
                startActivity(mine);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.favourites){
            Intent mine = new Intent(this,PodcastActivity.class);
            mine.putExtra("PodcastBundle","fav");
            startActivity(mine);
        }
        return super.onOptionsItemSelected(item);
    }
}
