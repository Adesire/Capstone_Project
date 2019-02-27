package com.example.android.poddle.fragments;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.poddle.MyMediaService;
import com.example.android.poddle.PlaybackWidget;
import com.example.android.poddle.PlaybackWidgetService;
import com.example.android.poddle.PodacastSelectedActivity;
import com.example.android.poddle.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;


public class PodcastAudioFragment extends Fragment implements Player.EventListener {

    private TextView episodeNowPlayingTitle;

    private static final String TAG = PodcastAudioFragment.class.getSimpleName();

    private PlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    Toolbar mToolbar;
    public static String IMAGE;
    static String PLAYER_STATE;
    public static boolean ON_LISTEN_CLICKED;
    PlaybackWidget mPlaybackWidget;
    Intent notification;

    String audio, title, desc;

    public static PodcastAudioFragment newInstance(String audioString, String title, String desc) {
        PodcastAudioFragment fragment = new PodcastAudioFragment();
        Bundle b = new Bundle();
        b.putString("Audio", audioString);
        b.putString("Audio_title", title);
        b.putString("Audio_desc", desc);
        fragment.setArguments(b);
        return fragment;
    }

    private PodacastSelectedActivity getActivityCast() {
        return (PodacastSelectedActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_podcast_audio, container, false);

        ImageView thumbnail = v.findViewById(R.id.playingThumbnail);
        Picasso.with(getContext()).load(IMAGE).into(thumbnail);

        MyMediaService.AUDIO_IMAGE = IMAGE;
        mPlaybackWidget = new PlaybackWidget();

        episodeNowPlayingTitle = v.findViewById(R.id.nowPlayingTitle);

        mPlayerView = (PlayerView) v.findViewById(R.id.playerView);

        initializeMediaSession();

        notification = new Intent(getActivity(), MyMediaService.class);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle b = getArguments();
        audio = b.getString("Audio");
        title = b.getString("Audio_title");
        desc = b.getString("Audio_desc");

        episodeNowPlayingTitle.setText(title);
        episodeNowPlayingTitle.setSelected(true);
        //episodeDescription.setText(desc);

        Log.e("TAG", audio);

        initializePlayer(audio);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            releasePlayer();
            mMediaSession.setActive(false);
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        //MyMediaService.mediaSession = mMediaSession;
        //PlaybackWidgetService.mediaSession = mMediaSession;

        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        mMediaSession.setCallback(new MySessionCallback());

        mMediaSession.setActive(true);
    }

    public static MediaSessionCompat myMediaSession(){
        return mMediaSession;
    }

    private void initializePlayer(String audioUri) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

            MyMediaService.player = mExoPlayer;
            PlaybackWidgetService.player = mExoPlayer;


            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(getContext(), "Poddle");

            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                    userAgent, null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true);

            /*MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(audioUri),
                    dataSourceFactory, Mp3Extractor.FACTORY,new Handler(),null);
*/
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(audioUri));
            MyMediaService.AUDIO_URL = audioUri;
            MyMediaService.AUDIO_TITLE = title;
            MyMediaService.AUDIO_DESC = desc;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("media_title", title);
            editor.apply();
            AppWidgetManager manager = AppWidgetManager.getInstance(getContext());
            ComponentName thisWidget = new ComponentName(getContext(), PlaybackWidget.class);
            mPlaybackWidget.onUpdate(getContext(), manager, manager.getAppWidgetIds(thisWidget));

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(ON_LISTEN_CLICKED);
        }
    }

    private void showNotification() {
        Util.startForegroundService(getActivityCast().getApplicationContext(), notification);
    }

    private void stopNotification() {
        getActivityCast().stopService(notification);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
            PLAYER_STATE = "PLAYING";
            Log.e("onPlayerStateChanged:", "PLAYING");
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
            Log.e("onPlayerStateChanged:", "PAUSED");
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());

        showNotification();

    }


    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(ON_LISTEN_CLICKED);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }

    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

}
