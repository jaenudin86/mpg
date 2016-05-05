package com.radicaldroids.mileage.Fragments;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.radicaldroids.mileage.Constants;
import com.radicaldroids.mileage.DataAccess.DataProvider;
import com.radicaldroids.mileage.Events.RefreshHistoryListViewEvent;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.radicaldroids.mileage.MyApplication;
import com.radicaldroids.mileage.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/23/2015.
 * Some of this class was taken from the examples at https://github.com/PhilJay/MPAndroidChart
 */
public class GraphFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private LineChart mChart;
    String TAG="graphFragment";
    ArrayList<Double> mPoints;
    private Tracker mTracker;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static GraphFragment newInstance(String s){
        GraphFragment graphFrag=new GraphFragment();

        Bundle args = new Bundle();
        args.putString("someString", s);
        graphFrag.setArguments(args);
        return graphFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication application=(MyApplication) getActivity().getApplication();
        mTracker=application.getTracker();
        sendAnalyticName();

        mPoints=new ArrayList<>();
//        String s=getArguments().getString("someString", "notFound");
        setRetainInstance(true);

        SharedPreferences sharedPrefs=getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        String currentVehicle=sharedPrefs.getString(Constants.SHARED_PREFS_CURRENT_VEHICLE,"null");
        if(currentVehicle.compareToIgnoreCase("null")!=0) {
            if (savedInstanceState == null) {
                getLoaderManager().initLoader(1, null, (LoaderManager.LoaderCallbacks) this);
            } else {
                getLoaderManager().restartLoader(1, null, (LoaderManager.LoaderCallbacks) this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart, container, false);
        mChart = (LineChart) view.findViewById(R.id.chart_view);

//        if(savedInstanceState==null) {
//        mChart.setOnChartGestureListener(this);
//        mChart.setOnChartValueSelectedListener(this);
            mChart.setDrawGridBackground(true);

            // no description text
            mChart.setDescription("");
            mChart.setNoDataTextDescription("You need to provide data for the chart.");

            // enable touch gestures
            mChart.setTouchEnabled(true);

            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);
            // mChart.setScaleXEnabled(true);
            // mChart.setScaleYEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(true);
            mChart.setBackgroundColor(Color.LTGRAY);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1);
//        leftAxis.addLimitLine(ll2);
//        leftAxis.setAxisMaxValue(220f);
//        leftAxis.setAxisMinValue(-50f);
            leftAxis.setStartAtZero(false);
            //leftAxis.setYOffset(20f);
            leftAxis.enableGridDashedLine(10f, 10f, 0f);

            XAxis xAxis = mChart.getXAxis();

            setData();
//        }

        return view;
    }
    private void setData() {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < mPoints.size(); i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i=0;i<mPoints.size();i++) {
            Double d=mPoints.get(i);
            yVals.add(new Entry(d.floatValue(), i));
        }

        String t="vals";
        for(int i=0;i<yVals.size();i++){
            t+=yVals.get(i);
        }
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "MPG");

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.BLUE);
        set1.setLineWidth(2f);
        set1.setCircleSize(4f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(16f);
//        set1.setFillAlpha(0);
        set1.setFillColor(Color.BLUE);
//        set1.setDrawFilled(true);
//        set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
//        Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL = new CursorLoader(getActivity().getApplicationContext(), Uri.parse(DataProvider.BASE_CONTENT_URI +"/mpg_data"), null, null, null, null);
        return CL;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, final Cursor cursor) {
        if(cursor!=null&&cursor.getCount()>1) {
            cursor.moveToLast();  //starting from end, moving forward
            do {
                Double value = cursor.getDouble(0);
                if (value != 0)
                    addEntry(value);
            } while (cursor.moveToPrevious());
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            cursor.close();
        }else{
            Snackbar.make(mChart,R.string.need_more_data2,Snackbar.LENGTH_LONG).show();
        }
    }

    private void addEntry(Double point) {
        LineData data=mChart.getData();
        if(data!=null) {
            LineDataSet set=data.getDataSetByIndex(0);

            if(set==null) {
                set=createSet();
                data.addDataSet(set);
            }
            data.addXValue("");
            data.addEntry(new Entry(point.floatValue(),set.getEntryCount()),0);

//            mChart.notifyDataSetChanged();
//            mChart.invalidate();
//            setData();
        }
    }
    private void deleteData(){
        Log.e("delete Data","deleteData: "+mChart.getData()+", average: "+mChart.getData().getAverage());
        LineData data=mChart.getData();
        if(data!=null) {
            Log.e("remove","removeDataset: "+data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount()-1)));
//            mChart.notifyDataSetChanged();
//            mChart.invalidate();
            Log.e("deleteData","Data deleted");
        }
        Log.e("delete Data","deleteData: "+mChart.getData()+", average: "+mChart.getData().getAverage());
    }

    private LineDataSet createSet() {
        LineDataSet set1=new LineDataSet(null,"MPG");

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.BLUE);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));
        return set1;
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(RefreshHistoryListViewEvent event){
        deleteData();
//        mChart.clear();
//        mChart = (LineChart) view.findViewById(R.id.chart_view);
        getLoaderManager().restartLoader(1, null, (LoaderManager.LoaderCallbacks) this);
//        setData();
    }
    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticName();
    }
    private void sendAnalyticName(){
        mTracker.setScreenName(getString(R.string.graph_fragment_analytic_tag));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}