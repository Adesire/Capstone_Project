package com.example.android.poddle.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.poddle.PodcastModel;
import com.example.android.poddle.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PodcastGridAdapter extends RecyclerView.Adapter<PodcastGridAdapter.PodcastViewHolder>{

    Context c;
    private ArrayList<PodcastModel> podcast;
    private PodcastItemClickedListener mListener;

    public PodcastGridAdapter(Context c, ArrayList<PodcastModel> podcast, PodcastItemClickedListener mListener){
        this.c = c;
        this.podcast = podcast;
        this.mListener = mListener;
    }

    public interface PodcastItemClickedListener{
        void onPodcastItemClicked(Bundle bundle);
    }

    @NonNull
    @Override
    public PodcastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.podcast_grid_item,parent,false);
        return new PodcastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PodcastViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        int length = 0;
        if (podcast == null)
            return length;

        return podcast.size();
    }

    class PodcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView thumbnail;
        TextView title,description;

        public PodcastViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.podcast_title);
            description = itemView.findViewById(R.id.podcast_description);
            itemView.setOnClickListener(this);
        }

        void bind(int itemPosition){

                String titlePod = podcast.get(itemPosition).getPodcastTitle();
                if(titlePod.length()>20){
                    titlePod = titlePod.substring(0,20)+"...";
                }
                String thumbnailURL = podcast.get(itemPosition).getPodcastThumbnail();
                String desc = podcast.get(itemPosition).getPodcastDesc();

                title.setText(titlePod);
                description.setText(desc);
                Picasso.with(c).load(thumbnailURL).into(thumbnail);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Bundle b = new Bundle();
            //b.putString("PODCAST_ITEM",podcast.get(clickedPosition).toString());
            b.putParcelable("PODCAST_ITEM",podcast.get(clickedPosition));
            mListener.onPodcastItemClicked(b);
        }
    }
}
