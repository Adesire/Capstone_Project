package com.example.android.poddle;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MyMediaService extends Service {

    public static SimpleExoPlayer player;
    private PlayerNotificationManager mPlayerNotificationManager;
    public static MediaSessionCompat mediaSession;
    private MediaSessionConnector mMediaSessionConnector;
    public static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    public static final int PLAYBACK_NOTIFICATION_ID = 1;
    public static final String MEDIA_SESSION_TAG = PodcastAudioFragment.class.getSimpleName();
    public static String AUDIO_URL;
    public static String AUDIO_TITLE;
    public static String AUDIO_DESC;
    public static String AUDIO_IMAGE;


    @Override
    public void onCreate() {
        super.onCreate();

        mPlayerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                this, PLAYBACK_CHANNEL_ID, R.string.playback_name, PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return AUDIO_TITLE;
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        PendingIntent contentPendingIntent = PendingIntent.getActivity(MyMediaService.this,0,
                                new Intent(MyMediaService.this,PodcastSelectedFragment.class),0);
                        return contentPendingIntent;
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return AUDIO_DESC;
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                         return getMyBitmap();
                    }
                }
        );

        mPlayerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId,notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                stopSelf();
            }
        });
        mPlayerNotificationManager.setPlayer(player);
        mediaSession = PodcastAudioFragment.myMediaSession();//new MediaSessionCompat(this,MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        mPlayerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());

        mMediaSessionConnector = new MediaSessionConnector(mediaSession);
        mMediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return getMyMediaDescription();
            }
        });
        mMediaSessionConnector.setPlayer(player,null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
        mMediaSessionConnector.setPlayer(null,null);
        mPlayerNotificationManager.setPlayer(null);
        player.release();
        player = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public static Bitmap getMyBitmap(){
        Bitmap image= null;
        try {
            URL url = new URL(AUDIO_IMAGE);
            InputStream inputStream = url.openConnection().getInputStream();
            image = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static MediaDescriptionCompat getMyMediaDescription() {
        Bundle extras = new Bundle();
        Bitmap bitmap = getMyBitmap();
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap);

        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap);
        return new MediaDescriptionCompat.Builder()
                .setMediaId(PodcastSelectedFragment.podcastID)
                .setIconBitmap(bitmap)
                .setTitle(AUDIO_TITLE)
                .setDescription(AUDIO_DESC)
                .setExtras(extras)
                .build();
    }

}
