package com.android.nikhil.analyse.widget

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService

/**
 * Created by NIKHIL on 05-01-2018.
 */

class WidgetRemoteViewsService : RemoteViewsService() {
  override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
    return WidgetRemoteViewsFactory(applicationContext)
  }
}
