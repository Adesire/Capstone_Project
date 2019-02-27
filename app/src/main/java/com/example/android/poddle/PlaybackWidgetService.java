package com.example.android.poddle;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.android.poddle.fragments.PodcastAudioFragment;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

import java.util.List;


public class PlaybackWidgetService extends MediaBrowserServiceCompat {

    public static SimpleExoPlayer player;
    public static MediaSessionCompat mediaSession;
    private MediaSessionConnector mMediaSessionConnector;
    MediaControllerCompat.TransportControls controls;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = PodcastAudioFragment.myMediaSession();

        if(mediaSession == null){
            Toast.makeText(this,"Please play podcast from app",
                    Toast.LENGTH_LONG).show();
        }else{
            controls = mediaSession.getController().getTransportControls();
            mediaSession.setActive(true);
            setSessionToken(mediaSession.getSessionToken());
            mMediaSessionConnector = new MediaSessionConnector(mediaSession);
            mMediaSessionConnector.setPlayer(player,null);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
        mMediaSessionConnector.setPlayer(null,null);
        player.release();
        player=null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null || intent.getAction() == null) {
            return super.onStartCommand(null, flags, startId);
        }

        if (intent.getAction().equals("PLAY")) {
            Log.e("SERVICE","Play button clicked!!");
            controls.play();
            reachWidget(0);
        }
        if(intent.getAction().equals("PAUSE")){
            Log.e("SERVICE","Pause button clicked!!");
            controls.pause();
            reachWidget(1);
        }

        if(intent.getAction().equals("REWIND")){
            Log.e("SERVICE","Rewind button clicked!!");
            controls.rewind();
            widgetRwd();
        }

        if(intent.getAction().equals("FAST_FORWARD")){
            Log.e("SERVICE","Ffwd button clicked!!");
            controls.fastForward();
            widgetFfwd();
        }

        return START_STICKY;
    }

    private void reachWidget(int x){
        Intent intent = new Intent(this, PlaybackWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), PlaybackWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        if(x==0){
            intent.putExtra("IS_PLAYING",true);
        }else if(x==1){
            intent.putExtra("IS_PLAYING",false);
        }
        sendBroadcast(intent);

    }

    private void widgetFfwd(){
        Intent intent = new Intent(this, PlaybackWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), PlaybackWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
    }

    private void widgetRwd(){
        Intent intent = new Intent(this, PlaybackWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), PlaybackWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(getString(R.string.app_name), rootHints);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }
}
