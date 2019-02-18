package com.example.android.poddle;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

/**
 * Implementation of App Widget functionality.
 */
public class PlaybackWidget extends AppWidgetProvider {

    public static String HEADING;
    SharedPreferences mSharedPreferences;

    public PlaybackWidget() {
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.playback_widget);

        Intent serviceIntentPlay = new Intent(context, PlaybackWidgetService.class);
        Intent serviceIntentPause = new Intent(context, PlaybackWidgetService.class);

        serviceIntentPlay.setAction("PLAY");
        serviceIntentPlay.setAction("PAUSE");
        serviceIntentPlay.setAction("FAST_FORWARD");

        //PendingIntent playPausePending = MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE);
        // PendingIntent.getService(context,0,serviceIntentPlay,0);
        PendingIntent playPending = PendingIntent.getService(context, 0, serviceIntentPlay, 0);
        PendingIntent ffwdPending = PendingIntent.getService(context, 0, serviceIntentPause, 0);
        //views.setRemoteAdapter(R.id.playbackControls,serviceIntent);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains("media_title")) {
            views.setTextViewText(R.id.widget_NowPlayingTitle, sharedPreferences.getString("media_title", "title"));
        }
        views.setImageViewBitmap(R.id.widget_PlayingThumbnail, MyMediaService.getMyBitmap());

        views.setOnClickPendingIntent(R.id.exo_play, playPending);
        views.setOnClickPendingIntent(R.id.exo_ffwd, ffwdPending);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

