package com.example.android.poddle.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.poddle.PodcastModel;

@Entity(tableName = "favourites_table")
public class FavouritePodcasts implements Parcelable{

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPodcastTitle() {
        return podcastTitle;
    }

    public void setPodcastTitle(String podcastTitle) {
        this.podcastTitle = podcastTitle;
    }

    public String getPodcastDesc() {
        return podcastDesc;
    }

    public void setPodcastDesc(String podcastDesc) {
        this.podcastDesc = podcastDesc;
    }

    public String getPodcastThumbnail() {
        return podcastThumbnail;
    }

    public void setPodcastThumbnail(String podcastThumbnail) {
        this.podcastThumbnail = podcastThumbnail;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String podcastTitle;
    private String podcastDesc;

    private String podcastThumbnail;

    private String pid;

    @Ignore
    public FavouritePodcasts(String podcastTitle,String podcastDesc,String podcastThumbnail,String pid){
        this.podcastTitle = podcastTitle;
        this.podcastDesc = podcastDesc;
        this.podcastThumbnail = podcastThumbnail;
        this.pid = pid;
    }

    public FavouritePodcasts(int id,String podcastTitle,String podcastDesc,String podcastThumbnail,String pid){

        this.id = id;
        this.podcastTitle = podcastTitle;
        this.podcastDesc = podcastDesc;
        this.podcastThumbnail = podcastThumbnail;
        this.pid = pid;
    }

    public FavouritePodcasts(Parcel in){
        String[] data = new String[4];
        int[] snum = new int[1];

        in.readStringArray(data);
        in.readIntArray(snum);

        this.podcastTitle = data[0];
        this.podcastDesc = data[1];
        this.podcastThumbnail = data[2];
        this.pid = data[3];
        this.id = snum[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.podcastTitle,this.podcastDesc,this.podcastThumbnail,this.pid
        });
        dest.writeIntArray(new int[]{this.id});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public FavouritePodcasts createFromParcel(Parcel in){
            return new FavouritePodcasts(in);
        }

        public FavouritePodcasts[] newArray(int size){
            return new FavouritePodcasts[size];
        }
    };

}
