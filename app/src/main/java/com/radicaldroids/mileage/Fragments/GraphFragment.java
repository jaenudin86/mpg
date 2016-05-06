package com.radicaldroids.mileage.Fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.radicaldroids.mileage.DataAccess.DataProvider;
import com.radicaldroids.mileage.Events.RefreshHistoryListViewEvent;
import com.radicaldroids.mileage.MyApplication;
import com.radicaldroids.mileage.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 5/5/2016.
 * Most of this class was taken from examples at the MPAndroidChart Github
 */
public class GraphFragment extends Fragment {

    private LineChart mChart;
    private String TAG="GraphFragment";
    private TextView mGraphWords;
    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication application=(MyApplication) getActivity().getApplication();
        mTracker=application.getTracker();
        sendAnalyticName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart, container, false);

        mChart = (LineChart) view.findViewById(R.id.chart_view);
        mGraphWords=(TextView) view.findViewById(R.id.graph_words);

        mChart.setViewPortOffsets(50, 30, 50, 0);
//        mChart.setExtraLeftOffset(15);
//        mChart.setExtraRightOffset(15);

        // no description text
        mChart.setDescription("");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        mChart.setPinchZoom(true);

        mChart.setBackgroundColor(Color.LTGRAY);
        mChart.setGridBackgroundColor(Color.LTGRAY);
        mChart.setDrawGridBackground(true);
        mChart.setBorderColor(255);

        XAxis x = mChart.getXAxis();
        x.setEnabled(false);

        YAxis y = mChart.getAxisLeft();

        y.setLabelCount(6, false);
        y.setTextColor(Color.BLUE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(true);
        y.setAxisLineColor(Color.BLUE);

        mChart.getAxisRight().setEnabled(false);

        setData();

        return view;
    }

    private void setData() {
        Cursor cursor=getActivity().getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI +"/mpg_data"),null,null,null,null);

        if(cursor!=null&&cursor.getCount()>1){
            ArrayList<String> xVals = new ArrayList<String>();
            for (int i = 1; i < cursor.getCount(); i++) {
                xVals.add(i + "");
            }

            ArrayList<Entry> yVals = new ArrayList<Entry>();

            int i=0;
            cursor.moveToLast();
            cursor.moveToPrevious();    //last entry is always 0 so skip it
            do {
                float value = cursor.getFloat(0);
                Log.e(TAG,"value: "+value);

                yVals.add(new Entry(value, i));
                i++;
            } while (cursor.moveToPrevious());
            cursor.close();

            LineDataSet set1;

            if (mChart.getData()!= null && mChart.getData().getDataSetCount() > 0) {
                mChart.notifyDataSetChanged();
            } else {
                // create a dataset and give it a type
                set1 = new LineDataSet(yVals, "DataSet 1");

                set1.setDrawCubic(true);
                set1.setCubicIntensity(0.2f);
                set1.setDrawCircles(true);
                set1.setDrawFilled(true);
                set1.setLineWidth(1.8f);
                set1.setCircleRadius(4f);
                set1.setCircleColor(Color.BLUE);
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setColor(Color.BLUE);
                set1.setFillColor(Color.BLUE);
                set1.setFillAlpha(100);
                set1.setDrawHorizontalHighlightIndicator(false);
                set1.setFillFormatter(new FillFormatter() {
                    @Override
                    public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                        return -10;
                    }
                });

                // create a data object with the datasets
                LineData data = new LineData(xVals, set1);
                data.setValueTextSize(18f);

                // set data
                mChart.setData(data);
            }
            if(mChart.getData()!=null) {
                mChart.getLegend().setEnabled(false);

                mChart.animateXY(750, 750);
                mChart.getData().setDrawValues(true);

            }
            mChart.invalidate();
        }else { //not enough data to create a graph

            Toast.makeText(getActivity(),R.string.need_more_data2,Toast.LENGTH_LONG).show();

            mGraphWords.setText(R.string.need_more_data2);

            LineData data=new LineData();
            mChart.setData(null);
            mChart.setData(data);
        }
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
        mChart.clear();
        setData();
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