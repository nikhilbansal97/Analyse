package com.android.nikhil.analyse.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.nikhil.analyse.R;

import static com.android.nikhil.analyse.widget.AnalyseWidget.ACTION_UPDATEWIDGET;

/**
 * Created by NIKHIL on 05-01-2018.
 */

public class WidgetRefreshService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    private static final String TAG = "WidgetRefreshService";
    
    public WidgetRefreshService() {
        super("WidgetRefreshService");
        Log.d(TAG, "WidgetRefreshService: constructor");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_UPDATEWIDGET)) {
            Log.d(TAG, "onHandleIntent: Intent handled.");
            AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
            int[] ids = manager.getAppWidgetIds(new ComponentName(getApplicationContext(), AnalyseWidget.class));
            manager.notifyAppWidgetViewDataChanged(ids, R.id.widgetListView);
        }
    }
}
