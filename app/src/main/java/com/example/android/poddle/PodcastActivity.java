package com.example.android.poddle;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.android.poddle.adapters.FavouritesAdapter;
import com.example.android.poddle.adapters.PodcastGridAdapter;
import com.example.android.poddle.data.AppDatabase;
import com.example.android.poddle.data.FavouritePodcasts;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PodcastActivity extends AppCompatActivity implements PodcastGridAdapter.PodcastItemClickedListener, FavouritesAdapter.ItemClickListener{

    static String URL;
    String name;
    int id;
    int i;
    static int x;
    ArrayList<PodcastModel> results;
    PodcastGridAdapter podcastAdapter;
    public FavouritesAdapter mFavouritesAdapter;

    RecyclerView podcastGrid;
    GridLayoutManager mLayoutManager;

    InterstitialAd mInterstitialAd;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);

        loadAds();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String data = getIntent().getStringExtra("PodcastBundle");
        final String query = getIntent().getStringExtra("search_results");
        //Log.e("DATA",data);

        podcastGrid = findViewById(R.id.podcastGrid);
        mLayoutManager = new GridLayoutManager(this,3);
        podcastGrid.setLayoutManager(mLayoutManager);

        results = new ArrayList<>();
        podcastAdapter = new PodcastGridAdapter(this,results,this);

        podcastGrid.setAdapter(podcastAdapter);
        podcastGrid.setHasFixedSize(true);

        mFavouritesAdapter = new FavouritesAdapter(this,this);
        db = AppDatabase.getInstance(getApplicationContext());

        if(!data.equals("") && x==0){
            try {
                JSONObject object = new JSONObject(data);
                name = object.optString("name");
                id = object.optInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            URL = "https://api.listennotes.com/api/v1/search?genre_ids="+id+"&language=English&offset=0&" +
                    "q="+name+"&sort_by_date=0&type=podcast";

            podcastNetworkCalls(URL,MainActivity.KEY);
        }else if(!data.equals("") && x==1){
            setupViewModel();
            Toast.makeText(this, R.string.remove_podcast,Toast.LENGTH_LONG).show();
            podcastGrid.setAdapter(mFavouritesAdapter);
            toolbar.setTitle(R.string.favourite);

        } else{

            URL = "https://api.listennotes.com/api/v1/search?q="+query+"&sort_by_date=0&type=podcast";
            podcastNetworkCalls(URL,MainActivity.KEY);
            toolbar.setTitle(R.string.searched_results);

            podcastGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(dy>0){
                        if(!podcastGrid.canScrollVertically(RecyclerView.FOCUS_DOWN)){
                            final String URL2 = "https://listennotes.p.mashape.com/api/v1/search?offset="+i+"&q="+query+"&sort_by_date=0&type=podcast";
                            Log.e("TAG for i",String.valueOf(i));
                            nextPageCall(URL2,MainActivity.KEY);
                        }
                    }
                }
            });
        }

    }

    public void setupViewModel(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getPodcast().observe(this, new Observer<List<FavouritePodcasts>>() {
            @Override
            public void onChanged(@Nullable List<FavouritePodcasts> favouritePodcasts) {
                mFavouritesAdapter.setFavouritePodcasts(favouritePodcasts);
                //Log.e("Favourite",favouritePodcasts.get(0).getPodcastTitle());
            }
        });
    }

    private void podcastNetworkCalls(String URL,String key){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key",key);
        client.get(URL,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray data = response.optJSONArray("results");
                i = response.optInt("next_offset");

                for(int j=0;j<data.length();j++){
                    JSONObject object = data.optJSONObject(j);
                    results.add(new PodcastModel(object.optString("title_original"),
                            object.optString("description_original"),object.optString("thumbnail"),object.optString("id")));
                    podcastAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void nextPageCall(String URL,String key){
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key",key);
        client.get(URL,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray podcastDataNext = response.optJSONArray("results");
                i = response.optInt("next_offset");

                Log.e("TAG for other i",String.valueOf(i));

                for(int j=0;j<podcastDataNext.length();j++){
                    JSONObject o = podcastDataNext.optJSONObject(j);
                    results.add(new PodcastModel(o.optString("title_original"),
                            o.optString("description_original"),o.optString("thumbnail"),o.optString("id")));
                    podcastAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    @Override
    public void onPodcastItemClicked(Bundle bundle) {

        //Log.e("PODCAST_INTENT",bundle.getString("PODCAST_ITEM"));
        if(mInterstitialAd.isLoaded()){
            Intent mine = new Intent(this,PodacastSelectedActivity.class);
            mine.putExtra("PODCAST_INTENT",bundle.getParcelable("PODCAST_ITEM"));
            startActivity(mine);
            mInterstitialAd.show();
        }

    }

    private void loadAds(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.insterstitial_ad_string));

        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(adRequest);
            }
        });
    }


    @Override
    public void onItemClicked(Bundle b) {
        onPodcastItemClicked(b);
    }

    @Override
    public void onItemLongClicked(final int pos) {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Remove from Favourites?")
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<FavouritePodcasts> movie = mFavouritesAdapter.getFavouritePodcasts();
                                db.favouriteDao().deletePodcastEntry(movie.get(pos));
                            }
                        });

                    }
                })
                .show();
    }
}
