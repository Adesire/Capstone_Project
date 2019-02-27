package com.example.android.poddle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.android.poddle.adapters.GenreGridAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements GenreGridAdapter.GridItemClickListener{

    GridLayoutManager mLayoutManager;
    RecyclerView genreView;
    FirebaseAnalytics mFirebaseAnalytics;
    TextView networkFail;
    ProgressBar load;
    public static String KEY = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkFail = (TextView) findViewById(R.id.no_network);
        load = (ProgressBar) findViewById(R.id.progressBar);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        genreView = (RecyclerView) findViewById(R.id.genreGrid);
        mLayoutManager = new GridLayoutManager(this,2);
        genreView.setLayoutManager(mLayoutManager);


        if(checkConnection()){
            load.setVisibility(View.VISIBLE);
            networkCalls("https://api.listennotes.com/api/v1/genres",KEY);
        }else {
            networkFail.setVisibility(View.VISIBLE);
        }



    }

    private boolean checkConnection(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo active = cm.getActiveNetworkInfo();
        if(active != null && active.getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        return false;
    }



    private void networkCalls(String URL,String key){
        load.setVisibility(View.INVISIBLE);
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


                new AsyncTaskSearch().execute(query);


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
            PodcastActivity.x = 1;
            startActivity(mine);
        }
        return super.onOptionsItemSelected(item);
    }

    public class AsyncTaskSearch extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            Intent mine = new Intent(MainActivity.this,PodcastActivity.class);
            //Log.e("AsyncTaskSearch","search successful");
            mine.putExtra("search_results",strings[0]);
            mine.putExtra("PodcastBundle","");
            startActivity(mine);
            return null;
        }
    }
}
