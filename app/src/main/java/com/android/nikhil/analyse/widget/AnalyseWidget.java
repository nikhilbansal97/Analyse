package com.android.nikhil.analyse.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.nikhil.analyse.R;

/**
 * Implementation of App Widget functionality.
 */
public class AnalyseWidget extends AppWidgetProvider {

    private static final String TAG = "AnalyseWidget";
    public static final String ACTION_UPDATEWIDGET = "com.android.nikhil.analyse.widget.ACTION_UPDATEWIDGET";

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.analyse_widget);

        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        views.setRemoteAdapter(R.id.widgetListView, intent);
        // Instruct the widget manager to update the widget
        Intent refreshIntent = new Intent(context, WidgetRefreshService.class);
        refreshIntent.setAction(ACTION_UPDATEWIDGET);
        PendingIntent refreshPendingIntent = PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetRefresh, refreshPendingIntent);
        Log.d(TAG, "updateAppWidget");
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.d(TAG, "onUpdate");
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

