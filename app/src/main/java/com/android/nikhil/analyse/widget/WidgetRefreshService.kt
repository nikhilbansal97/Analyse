package com.android.nikhil.analyse.widget

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.util.Log

import com.android.nikhil.analyse.R

import com.android.nikhil.analyse.widget.AnalyseWidget.Companion.ACTION_UPDATEWIDGET

/**
 * Created by NIKHIL on 05-01-2018.
 */

class WidgetRefreshService : IntentService("WidgetRefreshService") {
  init {
    Log.d(TAG, "WidgetRefreshService: constructor")
  }

  override fun onHandleIntent(intent: Intent?) {
    val action = intent!!.action
    if (action == ACTION_UPDATEWIDGET) {
      val manager = AppWidgetManager.getInstance(applicationContext)
      val ids = manager.getAppWidgetIds(ComponentName(applicationContext, AnalyseWidget::class.java))
      manager.notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)
    }
  }

  companion object {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    private val TAG = "WidgetRefreshService"
  }
}
