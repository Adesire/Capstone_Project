package com.example.android.poddle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class EpisodesGridAdapter extends RecyclerView.Adapter<EpisodesGridAdapter.EpisodeViewHolder>{

    private Context c;
    private ArrayList<PodcastModel> podcast;

    public EpisodesGridAdapter(Context c, ArrayList<PodcastModel> podcast){
        this.c = c;
        this.podcast = podcast;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.episode_item_view,parent,false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return podcast.size();
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder{
        TextView episodeTitle,episodeDescription;

        public EpisodeViewHolder(View itemView) {
            super(itemView);
            episodeTitle = itemView.findViewById(R.id.episode_title);
            episodeDescription = itemView.findViewById(R.id.episode_description);
        }
        void bind(int itemPosition){
            episodeTitle.setText(podcast.get(itemPosition).getEpisodeTitle());
            episodeDescription.setText(podcast.get(itemPosition).getEpisodeDescription());
        }
    }

}
