package com.radicaldroids.mileage;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;

/**
 * Created by Andrew on 4/26/2016.
 */
public class MyApplication extends Application {
    public Tracker mTracker;
    private String TAG="MyApplication";
    public ContainerHolder mContainerHolder;
    public TagManager mTagManager;

    // Get the Tag Manager
    public TagManager getTagManager () {
        if (mTagManager == null) {
            // create the TagManager, save it in mTagManager
            mTagManager = TagManager.getInstance(this);
        }
        return mTagManager;
    }

    // Set the ContainerHolder
    public void setContainerHolder (ContainerHolder containerHolder) {
        mContainerHolder = containerHolder;
    }

    // Get the ContainerHolder
    public ContainerHolder getContainerHolder() {
        return mContainerHolder;
    }

    public void startTracking(){
        if(mTracker==null){
            Log.e(TAG,"startTracking, mTracker=null");
            GoogleAnalytics ga=GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.track_app);
            ga.enableAutoActivityReports(this);
            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }
    public Tracker getTracker(){
        startTracking();
        return mTracker;
    }
}
