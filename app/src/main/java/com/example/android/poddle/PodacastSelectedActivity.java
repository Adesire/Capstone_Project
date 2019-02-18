package com.example.android.poddle;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.poddle.data.FavouritePodcasts;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PodacastSelectedActivity extends AppCompatActivity implements EpisodesGridAdapter.OnEpisodeClickedListener{

    Toolbar mToolbar;
    String originTitle;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podacast_selected);

        mToolbar = (Toolbar) findViewById(R.id.toolbarSelected);

        Intent data = getIntent();

        if(data.getParcelableExtra("PODCAST_INTENT") instanceof PodcastModel){
            PodcastModel model = data.getParcelableExtra("PODCAST_INTENT");
            originTitle = model.getPodcastTitle();
            image = model.getPodcastThumbnail();

            replaceSelectedPodcastActivityContent(PodcastSelectedFragment.NewInstance(model),false);
        }else{
            FavouritePodcasts fav = data.getParcelableExtra("PODCAST_INTENT");
            originTitle = fav.getPodcastTitle();
            image = fav.getPodcastThumbnail();
            replaceSelectedPodcastActivityContent(PodcastSelectedFragment.NewInstance2(fav),false);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
    }

    private void replaceSelectedPodcastActivityContent(Fragment fragment, boolean shouldAddBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content,fragment);
        if (shouldAddBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    private void replaceBottomFrameLayout(Fragment fragment, boolean shouldAddBackStack){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nowPlaying,fragment);
        if (shouldAddBackStack)
            transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onEpisodeClicked(final Bundle bundle) {
        /*replaceSelectedPodcastActivityContent(PodcastAudioFragment.newInstance(bundle.getString("PODCAST_AUDIO"),
                bundle.getString("PODCAST_TITLE"),bundle.getString("PODCAST_DESC")),true);

        PodcastAudioFragment.TITLE = originTitle;*/

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.episode_dialog,null);

        TextView episodeTitle = v.findViewById(R.id.episode_title);
        TextView episodeDescription = v.findViewById(R.id.episode_description);
        TextView episodeListen = v.findViewById(R.id.listenText);


        episodeTitle.setText(bundle.getString("PODCAST_TITLE"));
        episodeDescription.setText(bundle.getString("PODCAST_DESC"));


        mBuilder.setView(v);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        episodeListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG","Click click");
                Log.e("TAG2",image);
                PodcastAudioFragment.IMAGE = image;
                PodcastAudioFragment.ON_LISTEN_CLICKED = true;

                replaceBottomFrameLayout(PodcastAudioFragment.newInstance(bundle.getString("PODCAST_AUDIO"),
                        bundle.getString("PODCAST_TITLE"),bundle.getString("PODCAST_DESC")),false);

                dialog.cancel();
            }
        });


    }
}
