package com.example.android.poddle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.poddle.data.FavouritePodcasts;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder>{

    private Context mContext;
    List<FavouritePodcasts> podcast;

    private ItemClickListener mListener;

    FavouritesAdapter(Context mContext,ItemClickListener mListener){
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public interface ItemClickListener {
        void onItemClicked(Bundle b);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.podcast_grid_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(podcast == null){
            return 0;
        }
        return podcast.size();
    }

    public List<FavouritePodcasts> getFavouritePodcasts(){ return podcast;}

    public void setFavouritePodcasts(List<FavouritePodcasts> podcast){
        this.podcast = podcast;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView thumbnail;
        TextView title,description;

        public MyViewHolder(View itemView) {
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
            Picasso.with(mContext).load(thumbnailURL).into(thumbnail);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Bundle b = new Bundle();
            //b.putString("PODCAST_ITEM",podcast.get(clickedPosition).toString());
            b.putParcelable("PODCAST_ITEM",podcast.get(clickedPosition));
            mListener.onItemClicked(b);
        }
    }
}
