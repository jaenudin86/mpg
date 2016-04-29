package com.radicaldroids.mileage.Fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.radicaldroids.mileage.DataAccess.DialogInterfaces;
import com.radicaldroids.mileage.DataAccess.MySQLiteHelper;
import com.radicaldroids.mileage.Events.RefreshHistoryListViewEvent;
import com.radicaldroids.mileage.MyApplication;
import com.radicaldroids.mileage.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/22/2015.
 */
public class StatsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{

    private DialogInterfaces.DialogInterface mListener;
    private TextView mTitle;
    private TextView mMpgSinceLast;
    private TextView mMpgTotalView;
    private TextView mTotalMilesTravelled;
    private TextView mConclusion;
    private TextView mEmptyStats;
    private TextView mEmptyButton;
    String TAG="StatsFragment";
    private Tracker mTracker;

    public static StatsFragment newInstance(){
        StatsFragment overallStatsFragment=new StatsFragment();
        return overallStatsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null) {
            getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks) this);
            getLoaderManager().initLoader(1, null, (LoaderManager.LoaderCallbacks) this);
        }
        MyApplication application=(MyApplication) getActivity().getApplication();
        mTracker=application.getTracker();
        sendAnalyticName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_fragment, container, false);

        mTitle=(TextView) view.findViewById(R.id.stats_title);
        mMpgSinceLast = (TextView) view.findViewById(R.id.perfm_mpg_since_last);
        mMpgTotalView = (TextView) view.findViewById(R.id.perfm_mpg_overall);
        mTotalMilesTravelled = (TextView) view.findViewById(R.id.perfm_miles_travelled_total);
        mConclusion = (TextView) view.findViewById(R.id.perfm_conclusion);

        mEmptyStats =(TextView) view.findViewById(R.id.init_stats_message);
//        mEmptyButton =(Button) view.findViewById(R.id.empty_stats_button);
//        mEmptyButton.setOnClickListener(this);

        AdView mAdView=(AdView) view.findViewById(R.id.ad_view);
        AdRequest adRequest=new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        return view;
    }

    private String getLastDate(long number){
        Date date=new Date(number*1000);
        SimpleDateFormat format=new SimpleDateFormat("MMM/dd/yyyy");
//        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return format.format(date);
    }

//     Deleteable methods for internal logging only
//     @param
//
//    public void logData(){
//        Cursor c= dbHelper.getMilesColumn();
//
//        String names="";
//        for(String s: c.getColumnNames()){
//            names=names+s+", ";
//        }
//
//        Log.e("StatsFragment", "OverallstatsFrag columns returned: " + names);
//
//        if(c!=null){
//            if(c.moveToFirst()){
//                Log.e("row","row by row (first): "+c.getString(0));
//            }
//            if(c.moveToLast()){
//                Log.e("row","row by row (last): "+c.getString(0));
//            }
//            c.moveToFirst();
//            int n=0;
//            while(c.moveToNext()){
//                Log.e("row","row by row: "+n+", "+c.getString(0));
//                n++;
//            }
//            c.moveToLast();
//            Log.e("row", "row by row (last): " + c.getString(0));
//            if(c.moveToPrevious()) {
//                Log.e("row", "row by row (previous): " + c.getString(0));
//            }
//        }
////        c.close();
//    }
    public void onEvent(RefreshHistoryListViewEvent event){
        getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks) this);
        getLoaderManager().restartLoader(1, null, (LoaderManager.LoaderCallbacks) this);
    }
    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().registerSticky(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL=null;
        switch (id) {
            case 0:
                CL = new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.radicaldroids.mileage/mpg_data"), null, null, null, null);
                break;
            case 1:
                SharedPreferences mSharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                String curVeh=mSharedPrefs.getString("currentVehicle",null);
                if(curVeh!=null) {
                    CL = new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.radicaldroids.mileage/vehicle"), null, mSharedPrefs.getString("currentVehicle","null"), null, null);
                }
                break;
        }
        return CL;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.getCount() > 1) {
//                mEmptyButton.setVisibility(View.GONE);
                mEmptyStats.setVisibility(View.GONE);
                mTitle.setText(R.string.stats_title);
                setViewsVisible();

                switch (loader.getId()) {
                    case 0: //pull last known MPG record
                        data.moveToFirst();
                        Double mpg = data.getDouble(0);
//                        Log.e(TAG, "onLoadFinished mpg: " + mpg);
                        String mpgHtml="";
                        mpgHtml=mpg > 0 ? "Since last fill: <font size=\"3\" color=\"blue\">"+mpg + " mpg" : "<font size=\"3\" color=\"red\">Last entry is not correct!!";
                        mMpgSinceLast.setText(Html.fromHtml(mpgHtml));
                        break;
                    case 1: //calculate and display aggregate MPG since first record
                        data.moveToFirst();
                        int maxMiles = data.getInt(data.getColumnIndex(MySQLiteHelper.COLUMN_ODOMETER));
                        data.moveToLast();
                        int minMiles = data.getInt(data.getColumnIndex(MySQLiteHelper.COLUMN_ODOMETER));
                        int totalMiles = maxMiles - minMiles;
                        long lastDate=data.getInt(data.getColumnIndex(MySQLiteHelper.COLUMN_DATE));

                        data.moveToFirst();
                        Double galsTotal = 0.0;
                        Double priceTotal = 0.0;
                        do {
                            galsTotal += data.getDouble(data.getColumnIndex(MySQLiteHelper.COLUMN_QUANTITY));
                            priceTotal += data.getDouble(data.getColumnIndex(MySQLiteHelper.COLUMN_PRICE));
                        } while (data.moveToNext()&&!data.isLast());
                        priceTotal += data.getDouble(data.getColumnIndex(MySQLiteHelper.COLUMN_PRICE));

                        String mpgTotalHtml="Total Average: <font size=\"3\" color=\"blue\">"+String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(totalMiles / galsTotal)) + " mpg";
                        mMpgTotalView.setText(Html.fromHtml(mpgTotalHtml));
                        String totalMilesHtml="Total miles tracked: <font size=\"3\" color=\"blue\">" + NumberFormat.getNumberInstance(Locale.US).format(totalMiles);
                        mTotalMilesTravelled.setText(Html.fromHtml(totalMilesHtml));
                        String conclusionHtml="<font size=\"3\" color=\"blue\">"+NumberFormat.getCurrencyInstance().format(priceTotal)+ " </font><font size=\"3\" color=\"black\">since " + "</font><font size=\"3\" color=\"blue\">"+getLastDate(lastDate);
                        mConclusion.setText(Html.fromHtml(conclusionHtml));
                    break;
                }
            }else if(data.getCount()==1) {
//                mEmptyButton.setText(R.string.add_record_title);
//                mEmptyButton.setVisibility(View.VISIBLE);
                mEmptyStats.setText(R.string.init_message_one_more_entry);
                mEmptyStats.setVisibility(View.VISIBLE);
                setViewsInvisible();
            }else{
//                mEmptyButton.setText(R.string.add_record_title);
//                mEmptyButton.setVisibility(View.VISIBLE);
                mEmptyStats.setText(R.string.init_message_no_data);
                mEmptyStats.setVisibility(View.VISIBLE);
                setViewsInvisible();
            }
        }else{
            mEmptyStats.setText(R.string.init_message_no_vehicles);
//            mEmptyButton.setText(R.string.get_started);
            mEmptyStats.setVisibility(View.VISIBLE);
//            mEmptyButton.setVisibility(View.VISIBLE);
            setViewsInvisible();
        }
    }
    public void setViewsInvisible(){
        mTitle.setVisibility(View.INVISIBLE);
        mMpgSinceLast.setVisibility(View.INVISIBLE);
        mMpgTotalView.setVisibility(View.INVISIBLE);
        mTotalMilesTravelled.setVisibility(View.INVISIBLE);
        mConclusion.setVisibility(View.INVISIBLE);
    }
    public void setViewsVisible(){
        mTitle.setVisibility(View.VISIBLE);
        mMpgSinceLast.setVisibility(View.VISIBLE);
        mMpgTotalView.setVisibility(View.VISIBLE);
        mTotalMilesTravelled.setVisibility(View.VISIBLE);
        mConclusion.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onClick(View v) {
        mListener.pressInitialButtonAction();
    }
    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticName();
    }
    private void sendAnalyticName(){
        mTracker.setScreenName("StatsFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogInterfaces.DialogInterface) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}