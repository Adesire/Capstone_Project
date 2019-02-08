package com.example.android.poddle;

import android.os.Parcel;
import android.os.Parcelable;

public class PodcastModel implements Parcelable {
    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public String getEpisodeDescription() {
        return episodeDescription;
    }

    public String getEpisodeAudio() { return episodeAudio; }

    private String episodeTitle;
    private String episodeDescription;
    private String episodeAudio;

    public String getPodcastTitle() {
        return podcastTitle;
    }

    public String getPodcastDesc() {
        return podcastDesc;
    }

    public String getPodcastThumbnail() {
        return podcastThumbnail;
    }

    public String getId() {
        return id;
    }


    private String podcastTitle;
    private String podcastDesc;
    private String podcastThumbnail;
    private String id;

    public PodcastModel(String episodeTitle,String episodeDescription,String episodeAudio){
        this.episodeTitle = episodeTitle;
        this.episodeDescription = episodeDescription;
        this.episodeAudio = episodeAudio;
    }

    public PodcastModel(String podcastTitle,String podcastDesc,String podcastThumbnail,String id){
        this.podcastTitle = podcastTitle;
        this.podcastDesc = podcastDesc;
        this.podcastThumbnail = podcastThumbnail;
        this.id = id;
    }

    public PodcastModel(Parcel in){
        String[] data = new String[4];

        in.readStringArray(data);

        this.podcastTitle = data[0];
        this.podcastDesc = data[1];
        this.podcastThumbnail = data[2];
        this.id = data[3];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.podcastTitle,this.podcastDesc,this.podcastThumbnail,this.id
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public PodcastModel createFromParcel(Parcel in){
            return new PodcastModel(in);
        }

        public PodcastModel[] newArray(int size){
            return new PodcastModel[size];
        }
    };
}
