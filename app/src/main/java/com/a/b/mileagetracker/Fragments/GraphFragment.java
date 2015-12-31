package com.a.b.mileagetracker.Fragments;

import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a.b.mileagetracker.Events.RefreshHistoryListViewEvent;
import com.a.b.mileagetracker.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/23/2015.
 */
public class GraphFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private LineChart mChart;
    String TAG="graphFragment";
    ArrayList<Double> mPoints;

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
        mPoints=new ArrayList<>();
        String s=getArguments().getString("someString", "notFound");
        setRetainInstance(true);   //tends to cause problems on rotate

        SharedPreferences sharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String currentVehicle=sharedPrefs.getString("currentVehicle","null");
        if(currentVehicle.compareToIgnoreCase("null")!=0) {
            if (savedInstanceState == null) {
                getLoaderManager().initLoader(1, null, (LoaderManager.LoaderCallbacks) this);
            } else {
                getLoaderManager().restartLoader(1, null, (LoaderManager.LoaderCallbacks) this);
            }
        }else{
//            Snackbar.make(R.id.fragment_holder,R.string.need_more_data1,Snackbar.LENGTH_LONG).setDuration(7000).show();
            Toast.makeText(getActivity().getApplicationContext(),R.string.need_more_data1,Toast.LENGTH_LONG).show();
            Log.e(TAG,"empty graph");
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
            mChart.setBackgroundColor(Color.GRAY);

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

//        setData(6, 20);
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
        Log.e(TAG,"yVals: "+ t);


//        for (int i = 0; i < count; i++) {//
//            float mult = (range + 1);
//            float val = (float) (Math.random() * mult) + 3;// + (float)
//            // ((mult *
//            // 0.1) / 10);
//            yVals.add(new Entry(val, i));
//        }

//        yVals.add(new Entry((float)25.36,1));
//        yVals.add(new Entry((float)26.36,2));
//        yVals.add(new Entry((float)24.36,3));
//        yVals.add(new Entry((float)23.36,4));
//        yVals.add(new Entry((float) 28.36, 5));

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "MPG");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

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
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL = new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.a.b.mileagetracker/mpg_data"), null, null, null, null);
        Log.e(TAG,"oncreateLoader");
        return CL;
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, final Cursor data) {
//        data.moveToFirst();
//
//        if(data.getCount()>1){
//            do{
//                Double f= data.getDouble(0);
//                Log.e(TAG,"onFinishedLoader: "+data.getDouble(0)+", "+f);
//                mPoints.add(f);
//                Log.e(TAG,"onfinshedloader mPoints: "+mPoints.iterator().next().toString());
//            }while(data.moveToNext());
//        }
//        String t="vals";
//        for(int i=0;i<mPoints.size();i++){
//            t=t+mPoints.get(0);
//        }
//        Log.e(TAG, "onLoadFinished print t: " + t);

//        feedMultiple();

        if(data.getCount()>1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        data.moveToFirst();
//                        do {
//                            Double value = data.getDouble(0);
//                            if (value != 0)
//                                addEntry(value);
//                        } while (data.moveToNext());
                        data.moveToLast();
                        do {
                            Double value = data.getDouble(0);
                            if (value != 0)
                                addEntry(value);
                        } while (data.moveToPrevious());
                        }
                    });
                }
            }).start();
        }else{
            Snackbar.make(mChart,R.string.need_more_data2,Snackbar.LENGTH_LONG).setDuration(5000).show();
            Log.e(TAG,"empty graph");
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

            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }
    private void deleteData(){
        LineData data=mChart.getData();
        if(data!=null) {
            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount()-1));
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    private LineDataSet createSet() {
        LineDataSet set1=new LineDataSet(null,"Dynamic data");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
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
        getLoaderManager().restartLoader(1, null, (LoaderManager.LoaderCallbacks) this);
//        setData();
    }
}