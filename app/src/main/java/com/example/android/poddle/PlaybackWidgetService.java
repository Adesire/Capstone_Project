package com.example.android.poddle;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;

import static com.example.android.poddle.MyMediaService.getMyMediaDescription;

public class PlaybackWidgetService extends Service {

    public static SimpleExoPlayer player;
    public static MediaSessionCompat mediaSession;
    private MediaSessionConnector mMediaSessionConnector;

    @Override
    public void onCreate() {
        super.onCreate();
        if(mediaSession == null){
            Toast.makeText(this,"Please play podcast from app",
                    Toast.LENGTH_LONG).show();
        }else{
            mediaSession.setCallback(new MySessionCallback());
            mediaSession.setActive(true);
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
            //make whatever you want (play/stop song, next/previous etc)
            Log.e("SERVICE","A button clicked!!");
            player.setPlayWhenReady(true);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MySessionCallback extends MediaSessionCompat.Callback{
        @Override
        public void onPlay() {
            player.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            player.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            player.seekTo(0);
        }

    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mediaSession,intent);
        }
    }

}
