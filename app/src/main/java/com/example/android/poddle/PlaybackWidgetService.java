package com.example.android.poddle;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;

import java.util.List;

import static com.example.android.poddle.MyMediaService.getMyMediaDescription;

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
            //make whatever you want (play/stop song, next/previous etc)
            Log.e("SERVICE","Play button clicked!!");
            controls.play();
            reachWidget(0);
            //player.setPlayWhenReady(true);
        }
        if(intent.getAction().equals("PAUSE")){
            Log.e("SERVICE","Pause button clicked!!");
            controls.pause();
            reachWidget(1);
        }
        /*Log.e("SERVICE","A button clicked!!");
        MediaButtonReceiver.handleIntent(mediaSession,intent);*/
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
