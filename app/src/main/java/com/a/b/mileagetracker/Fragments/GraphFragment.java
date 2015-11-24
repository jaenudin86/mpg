package com.a.b.mileagetracker.Fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a.b.mileagetracker.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/23/2015.
 */
public class GraphFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private LineChart mChart;
    String TAG="graphFragment";
    ArrayList<Double> mPoints;

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
        getLoaderManager().initLoader(1, null, (LoaderManager.LoaderCallbacks) this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.graph_fragment, container, false);
//        TextView message=(TextView) view.findViewById(R.id.message_received);
//        message.setText(getArguments().getString("someString", "not found"));



        View view=inflater.inflate(R.layout.chart, container, false);
        mChart=(LineChart)view.findViewById(R.id.chart_view);
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


//        for (int i = 0; i < count; i++) {
//
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
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
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

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL = new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.a.b.mileagetracker/mpg_data"), null, null, null, null);
        Log.e(TAG,"oncreateLoader");
        return CL;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();


        if(data.getCount()>1){
            do{
                Double f= data.getDouble(0);
                Log.e(TAG,"onFinishedLoader: "+data.getDouble(0)+", "+f);
                mPoints.add(f);
                Log.e(TAG,"onfinshedloader mPoints: "+mPoints.iterator().next().toString());
            }while(data.moveToNext());
        }
        String t="vals";
        for(int i=0;i<mPoints.size();i++){
            t=t+mPoints.get(0);
        }
        Log.e(TAG,"onLoadFinished print t: "+t);

//        data.moveToFirst();
//        ArrayList<String> xVals = new ArrayList<String>();
//        for (int i = 0; i < 5; i++) {
//            xVals.add((i) + "");
//        }
//        ArrayList<Entry> yVals = new ArrayList<Entry>();
//        int i=0;
//        if(data.getCount()>1) {
////            do {
////                xVals.add((i) + "");
////                yVals.add(new Entry((float)3.0,i));
////
////                i++;
////
////                Log.e(TAG, "onLoadFinished cursor: " + data.getString(0));
////            } while (data.moveToNext());
//
//            yVals.add(new Entry((float)25.36,1));
//            yVals.add(new Entry((float)26.36,2));
//            yVals.add(new Entry((float)24.36,3));
//            yVals.add(new Entry((float)23.36,4));
//            yVals.add(new Entry((float) 28.36, 5));
//
//
//
//            Log.e(TAG,"onloadfinished exited do-while loop");
//            LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
//            // set1.setFillAlpha(110);
//            // set1.setFillColor(Color.RED);
//
//            // set the line to be drawn like this "- - - - - -"
//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
//            set1.setColor(Color.BLACK);
//            set1.setCircleColor(Color.BLACK);
//            set1.setLineWidth(1f);
//            set1.setCircleSize(3f);
//            set1.setDrawCircleHole(false);
//            set1.setValueTextSize(9f);
//            set1.setFillAlpha(65);
//            set1.setFillColor(Color.BLACK);
////        set1.setDrawFilled(true);
//            // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
//            // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));
//
//            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
//            dataSets.add(set1); // add the datasets
//
//            // create a data object with the datasets
//            LineData lineData = new LineData(xVals, dataSets);
//
//            // set data
//            mChart.setData(lineData);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
