/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.radicaldroids.mileage.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.radicaldroids.mileage.DataAccess.DataProvider;
import com.radicaldroids.mileage.DataAccess.SQLiteHelper;
import com.radicaldroids.mileage.MainActivity;
import com.radicaldroids.mileage.R;

public class WidgetIntentService extends IntentService {

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,WidgetProvider.class));

        //retrieving the latest MPG data
        Cursor data= getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI +"/mpg_data"),null,null,null,null);

        Double lastMpg;
        String widgetStats;
        if(data!=null&&data.getCount()>1) {
            data.moveToFirst();
            lastMpg = data.getDouble(0);
            widgetStats=lastMpg>0?lastMpg+" MPG":"Incomplete Data";
            data.close();
        }else{
            widgetStats="Need More Data";
        }

        //retrieving odometer data
        Cursor miles = getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI +"/current_vehicle"),new String[]{SQLiteHelper.COLUMN_ODOMETER},null,null,null);
        int totalMiles=0;
        if(miles!=null&&miles.getCount()>0) {
            miles.moveToFirst();
            int maxMiles = miles.getInt(miles.getColumnIndex(SQLiteHelper.COLUMN_ODOMETER));
            miles.moveToLast();
            int minMiles = miles.getInt(miles.getColumnIndex(SQLiteHelper.COLUMN_ODOMETER));
            totalMiles= maxMiles - minMiles;
            miles.close();
        }

        for (int appWidgetId : appWidgetIds) {

            int layoutId = R.layout.widget;

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, "MPG widget");
            }
            views.setTextViewText(R.id.last_mpg_value, widgetStats);
            views.setTextViewText(R.id.miles_tracked, totalMiles>0? totalMiles+" miles tracked":null);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
}