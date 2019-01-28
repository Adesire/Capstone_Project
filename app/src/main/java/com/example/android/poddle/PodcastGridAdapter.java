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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PodcastGridAdapter extends RecyclerView.Adapter<PodcastGridAdapter.PodcastViewHolder>{

    Context c;
    JSONArray mJSONArray;
    PodcastItemClickedListener mListener;

    public PodcastGridAdapter(Context c,JSONArray mJSONArray, PodcastItemClickedListener mListener){
        this.c = c;
        this.mJSONArray = mJSONArray;
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
        if (mJSONArray == null)
            return length;

        return mJSONArray.length();
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

            try {
                JSONObject object = mJSONArray.getJSONObject(itemPosition);
                String titlePod = object.optString("title_original");
                if(titlePod.length()>20){
                    titlePod = titlePod.substring(0,20)+"...";
                }
                String thumbnailURL = object.optString("thumbnail");
                String desc = object.optString("description_original");

                title.setText(titlePod);
                description.setText(desc);
                Picasso.with(c).load(thumbnailURL).into(thumbnail);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Bundle b = new Bundle();
            b.putString("PODCAST_ITEM",mJSONArray.optJSONObject(clickedPosition).toString());
            mListener.onPodcastItemClicked(b);
        }
    }
}
