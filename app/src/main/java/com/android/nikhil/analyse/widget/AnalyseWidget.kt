package com.android.nikhil.analyse.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

import com.android.nikhil.analyse.R

/**
 * Implementation of App Widget functionality.
 */
class AnalyseWidget : AppWidgetProvider() {

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    // There may be multiple widgets active, so update all of them
    for (appWidgetId in appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId)
    }
  }

  override fun onEnabled(context: Context) {
    // Enter relevant functionality for when the first widget is created
  }

  override fun onDisabled(context: Context) {
    // Enter relevant functionality for when the last widget is disabled
  }

  companion object {

    val ACTION_UPDATEWIDGET = "com.android.nikhil.analyse.widget.ACTION_UPDATEWIDGET"

    private fun updateAppWidget(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetId: Int
    ) {

      val views = RemoteViews(context.packageName, R.layout.analyse_widget)

      val intent = Intent(context, WidgetRemoteViewsService::class.java)
      views.setRemoteAdapter(R.id.widgetListView, intent)
      // Instruct the widget manager to update the widget
      val refreshIntent = Intent(context, WidgetRefreshService::class.java)
      refreshIntent.action = ACTION_UPDATEWIDGET
      val refreshPendingIntent = PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
      views.setOnClickPendingIntent(R.id.widgetRefresh, refreshPendingIntent)
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }
}

