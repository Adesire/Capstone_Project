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
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

/**
 * Implementation of App Widget functionality.
 */
public class PlaybackWidget extends AppWidgetProvider {

    private boolean playing;

    public PlaybackWidget() {
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.playback_widget);
        Intent intent = new Intent(context,PlaybackWidgetService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_Parent,pendingIntent);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains("media_title")) {
            views.setTextViewText(R.id.widget_NowPlayingTitle, sharedPreferences.getString("media_title", "title"));
        }
        views.setImageViewBitmap(R.id.widget_PlayingThumbnail, MyMediaService.getMyBitmap());


        if(playing){
            views.setViewVisibility(R.id.widget_play, View.GONE);
            views.setViewVisibility(R.id.widget_pause, View.VISIBLE);
            views.setOnClickPendingIntent(R.id.widget_pause, getPendingIntent(context,"PAUSE"));
        }else{
            views.setViewVisibility(R.id.widget_play, View.VISIBLE);
            views.setViewVisibility(R.id.widget_pause, View.GONE);
            views.setOnClickPendingIntent(R.id.widget_play, getPendingIntent(context,"PLAY"));
        }

        views.setOnClickPendingIntent(R.id.widget_rew,getPendingIntent(context,"REWIND"));
        views.setOnClickPendingIntent(R.id.widget_ffwd,getPendingIntent(context,"FAST_FORWARD"));


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private PendingIntent getPendingIntent (Context c, String action){
        Intent intent = new Intent(c, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(c, 0, intent, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

            playing = intent.getBooleanExtra("IS_PLAYING",false);

            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }

        if(action.equals("PLAY")){
            Intent play = new Intent(context,PlaybackWidgetService.class);
            play.setAction("PLAY");
            context.startService(play);
        }
        if(action.equals("PAUSE")){
            Intent pause = new Intent(context,PlaybackWidgetService.class);
            pause.setAction("PAUSE");
            context.startService(pause);
        }

        if(action.equals("REWIND")){
            Intent play = new Intent(context,PlaybackWidgetService.class);
            play.setAction("REWIND");
            context.startService(play);
        }
        if(action.equals("FAST_FORWARD")){
            Intent pause = new Intent(context,PlaybackWidgetService.class);
            pause.setAction("FAST_FORWARD");
            context.startService(pause);
        }

        //super.onReceive(context, intent);
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

