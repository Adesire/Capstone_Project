package com.example.android.poddle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GenreGridAdapter extends RecyclerView.Adapter<GenreGridAdapter.GenreViewHolder>{

    private Context c;
    private JSONArray mJSONArray;

    int id;
    final private GridItemClickListener mListener;

    public GenreGridAdapter(Context c, JSONArray mJSONArray,GridItemClickListener mListener){
        this.c = c;
        this.mJSONArray = mJSONArray;
        this.mListener = mListener;
    }

    public interface GridItemClickListener{
        void onGridItemClickListener(Bundle bundle);
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item,parent,false);

        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        int length = 0;
        if (mJSONArray == null)
            return length;

        return mJSONArray.length();
    }

    class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView genre;

        public GenreViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            genre = (TextView)itemView.findViewById(R.id.genre);
        }

        void bind(int itemPosition){
            try{

               JSONObject genreObject = mJSONArray.getJSONObject(itemPosition);
               String genreName = genreObject.optString("name");

                genre.setText(genreName);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Bundle b = new Bundle();
            b.putString("GENRE_OBJECT",mJSONArray.optJSONObject(clickedPosition).toString());
            mListener.onGridItemClickListener(b);
        }
    }
}
