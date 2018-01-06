package com.android.nikhil.analyse.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by NIKHIL on 05-01-2018.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {
    private static final String TAG = "WidgetRemoteViewsServic";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory");
        return new WidgetRemoteViewsFactory(getApplicationContext());
    }
}
