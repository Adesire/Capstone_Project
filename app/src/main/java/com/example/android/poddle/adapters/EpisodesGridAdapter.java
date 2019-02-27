package com.example.android.poddle.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.poddle.PodcastModel;
import com.example.android.poddle.R;

import java.util.ArrayList;

public class EpisodesGridAdapter extends RecyclerView.Adapter<EpisodesGridAdapter.EpisodeViewHolder>{

    private Context c;
    private ArrayList<PodcastModel> podcast;
    private OnEpisodeClickedListener mListener;

    public EpisodesGridAdapter(Context c, ArrayList<PodcastModel> podcast,OnEpisodeClickedListener mListener){
        this.c = c;
        this.podcast = podcast;
        this.mListener = mListener;
    }

    public interface OnEpisodeClickedListener{
        void onEpisodeClicked(Bundle bundle);
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

    class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView episodeTitle,episodeDescription;

        public EpisodeViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            episodeTitle = itemView.findViewById(R.id.episode_title);
            episodeDescription = itemView.findViewById(R.id.episode_description);
        }
        void bind(int itemPosition){
            episodeTitle.setText(podcast.get(itemPosition).getEpisodeTitle());
            episodeDescription.setText(podcast.get(itemPosition).getEpisodeDescription());
        }

        @Override
        public void onClick(View v) {
            int clickedItemPosition = getAdapterPosition();
            Bundle b = new Bundle();
            b.putString("PODCAST_AUDIO",podcast.get(clickedItemPosition).getEpisodeAudio());
            b.putString("PODCAST_TITLE",podcast.get(clickedItemPosition).getEpisodeTitle());
            b.putString("PODCAST_DESC",podcast.get(clickedItemPosition).getEpisodeDescription());

            mListener.onEpisodeClicked(b);
        }
    }

}
