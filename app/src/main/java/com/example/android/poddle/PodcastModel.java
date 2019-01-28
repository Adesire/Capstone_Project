package com.example.android.poddle;

public class PodcastModel {
    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public String getEpisodeDescription() {
        return episodeDescription;
    }

    private String episodeTitle;
    private String episodeDescription;

    public PodcastModel(String episodeTitle,String episodeDescription){
        this.episodeTitle = episodeTitle;
        this.episodeDescription = episodeDescription;
    }
}
