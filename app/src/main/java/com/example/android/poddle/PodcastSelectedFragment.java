package com.example.android.poddle;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.poddle.data.AppDatabase;
import com.example.android.poddle.data.FavouritePodcasts;
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
    Button favourite;
    static String originTitle,originDescription,imageURL,podcastID;
    RecyclerView episodesGrid;
    LinearLayoutManager mLayoutManager;
    private CollapsingToolbarLayout mToolbarLayout;
    private Toolbar mToolbar;
    int i;
    ArrayList<PodcastModel> episodes;
    EpisodesGridAdapter episodeAdapter;
    ProgressBar loading;
    FloatingActionButton fab;

    static String MY_DB_TITLE;

    private AppDatabase fdb;

    EpisodesGridAdapter.OnEpisodeClickedListener mListener;


    public static PodcastSelectedFragment NewInstance(PodcastModel podcastData){
        PodcastSelectedFragment fragment = new PodcastSelectedFragment();
        Bundle b = new Bundle();
        b.putParcelable("PODCAST_DATA",podcastData);
        fragment.setArguments(b);
        return fragment;
    }

    public static PodcastSelectedFragment NewInstance2(FavouritePodcasts podcastData){
        PodcastSelectedFragment fragment = new PodcastSelectedFragment();
        Bundle b = new Bundle();
        b.putParcelable("PODCAST_DATA",podcastData);
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
        fab = v.findViewById(R.id.floatingActionButton);
        favourite = v.findViewById(R.id.favourite);

        mToolbarLayout = (CollapsingToolbarLayout)v.findViewById(R.id.collapsing_toolbar_layout);
        mToolbar = v.findViewById(R.id.app_bar);

        mLayoutManager = new LinearLayoutManager(getActivityCast());
        episodesGrid.setLayoutManager(mLayoutManager);

        episodes = new ArrayList<>();

        episodeAdapter = new EpisodesGridAdapter(getActivityCast(),episodes,mListener);
        episodesGrid.setAdapter(episodeAdapter);

        episodesGrid.setHasFixedSize(true);

        fdb = AppDatabase.getInstance(getActivityCast());

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final Bundle dataBundle = getArguments();
        if(dataBundle.getParcelable("PODCAST_DATA") instanceof PodcastModel){
            PodcastModel data = dataBundle.getParcelable("PODCAST_DATA");

            originTitle = data.getPodcastTitle();
            originDescription = data.getPodcastDesc();
            podcastID = data.getId();
            imageURL = data.getPodcastThumbnail();

            compareWithDatabase(podcastID);

        }else{
            FavouritePodcasts data = dataBundle.getParcelable("PODCAST_DATA");

            originTitle = data.getPodcastTitle();
            originDescription = data.getPodcastDesc();
            podcastID = data.getPid();
            imageURL = data.getPodcastThumbnail();

            fab.setVisibility(View.INVISIBLE);
            favourite.setActivated(true);
        }
        //}
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


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavouriteClicked();
                if(!favourite.isActivated())
                    Toast.makeText(getActivityCast(),"Added to Favourites",Toast.LENGTH_SHORT).show();
                favourite.setText(R.string.unfavourite);
                favourite.setActivated(!favourite.isActivated());

                if(!favourite.isActivated()){

                    favourite.setText(R.string.favourite);
                    Toast.makeText(getActivityCast(),"Removed from Favourites",Toast.LENGTH_SHORT).show();

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            fdb.favouriteDao().deletePodcast(podcastID);
                        }
                    });
                }

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (EpisodesGridAdapter.OnEpisodeClickedListener) context;
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
                    episodes.add(new PodcastModel(o.optString("title_original"),o.optString("description_original"),o.optString("audio")));
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
                        episodes.add(new PodcastModel(o.optString("title_original"),o.optString("description_original"),o.optString("audio")));
                        episodeAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    private void onFavouriteClicked(){

        final FavouritePodcasts podcast = new FavouritePodcasts(originTitle,originDescription,imageURL,podcastID);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                fdb.favouriteDao().insertFav(podcast);

            }
        });
    }

    private void compareWithDatabase(final String pid){

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String title = fdb.favouriteDao().getFavouriteTitle(pid);
                  if(originTitle.equals(title)){
                        getActivityCast().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                favourite.setText(R.string.unfavourite);
                                favourite.setActivated(true);
                                onFavouriteClicked();
                            }
                        });

                    }

            }
        });

    }

}
