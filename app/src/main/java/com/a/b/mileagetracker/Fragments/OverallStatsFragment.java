package com.a.b.mileagetracker.Fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.Events.RefreshHistoryListViewEvent;
import com.a.b.mileagetracker.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/22/2015.
 */
public class OverallStatsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public MySQLiteHelper dbHelper;
    private int mMilesRecent=0;
    private int mMilesTotal=0;
    private TextView mpgSinceLast;
    private TextView mpgTotalView;
    private TextView milesTravelled;
    private TextView totalMilesTravelled;
    private TextView totalAmountSpent;
    private TextView conclusion;
    private Double mpgRecent;
    private Double mpgTotal;
    String TAG="OverallStatsFragment";

    public static OverallStatsFragment newInstance(){
        OverallStatsFragment overallStatsFragment=new OverallStatsFragment();
        return overallStatsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks) this);
        getLoaderManager().initLoader(1, null, (LoaderManager.LoaderCallbacks) this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.overall_stats_fragment, container, false);

        mpgSinceLast = (TextView) view.findViewById(R.id.perfm_mpg_since_last);
        mpgTotalView = (TextView) view.findViewById(R.id.perfm_mpg_overall);
        milesTravelled = (TextView) view.findViewById(R.id.perfm_miles_travelled);
        totalMilesTravelled = (TextView) view.findViewById(R.id.perfm_miles_travelled_total);
        totalAmountSpent = (TextView) view.findViewById(R.id.perfm_spent_total);
        conclusion = (TextView) view.findViewById(R.id.perfm_conclusion);

//        if(savedInstanceState==null){
//            displayCurrentVehicleStats();
//        }

        return view;
    }
    private void displayCurrentVehicleStats(){
        getLoaderManager().restartLoader(0,null,(LoaderManager.LoaderCallbacks) this);
        getLoaderManager().restartLoader(1,null,(LoaderManager.LoaderCallbacks) this);
//        Cursor c = dbHelper.getAllData().getCount();
//        if(c!=null&& c.getCount()?>0) {
        if(dbHelper.getAllData()!=null&&dbHelper.getAllData().getCount()>0){
            displayResults();
//            logData();
        }else{
//            noRecordForCurrentVehicle();
        }
    }

    private void displayResults(){
//        DecimalFormat decim=new DecimalFormat("0.00");
        mMilesRecent = getMilesSinceLast();
        mMilesTotal = getMilesTotal();
//        mpgRecent=getMpgRecent();
        mpgTotal=getMpgTotal();


//        mpgSinceLast.setText((mpgRecent>0?"Most recent: "+mpgRecent+" mpg": null));
//        mpgTotalView.setText((mpgTotal>0?"Total: "+mpgTotal+" mpg":"Need 2 or more data points to calculate MPG. Please Add another record"));
//        milesTravelled.setText((mpgRecent>0?"Miles since last record: " + mpgRecent:null));
//        totalMilesTravelled.setText((mMilesTotal>0?"Total miles tracked: " + NumberFormat.getNumberInstance(Locale.US).format(mMilesTotal):null));
//        totalAmountSpent.setText(NumberFormat.getCurrencyInstance().format(dbHelper.getTotalAmountSpent())+ " since "+getLastDate());
//        totalAmountSpent.setText("$ " + decim.format(dbHelper.getTotalAmountSpent()) + " since "+getLastDate());
    }
//    private void noRecordForCurrentVehicle(){
//        milesTravelled.setText("No data collected for selected vehicle!");
//        totalAmountSpent.setText("Click \"Add Record\" to get started");
//    }

//    private Double getMpgRecent(){
//        Double gallons;
//        Cursor cQuant=dbHelper.getQuantityColumn();
//        DecimalFormat df=new DecimalFormat("#.###");
//        if(cQuant!=null && cQuant.moveToLast()){
//            gallons=Double.parseDouble(cQuant.getString(0));
//            return Double.parseDouble(df.format(mMilesRecent / gallons));
//        }else{
//            return 0.0;
//        }
//    }
//    private Double getMpgRecent(){
//        return 0.0;
//    }

    private Double getMpgTotal(){
        Double gallons;
        DecimalFormat df=new DecimalFormat("#.###");
        Cursor galCur=dbHelper.getSumGallons();
        galCur.moveToFirst();
        gallons=Double.parseDouble(galCur.getString(0));
        return Double.parseDouble(df.format(mMilesTotal/gallons));
    }

    private int getMilesTotal(){
        int last,first;
        Cursor cMiles=dbHelper.getMilesColumn();
        if(cMiles.getCount()>1 && cMiles.moveToLast()) {
            last = Integer.parseInt(cMiles.getString(0));
            cMiles.moveToFirst();
            first = Integer.parseInt(cMiles.getString(0));
            cMiles.close();
            return last - first;
        }else{
            cMiles.close();
            return 0;
        }
    }
    private int getMilesSinceLast(){
        int last,previous;
        Cursor cMiles=dbHelper.getMilesColumn();
        if(cMiles!=null && cMiles.moveToLast()&&cMiles.getCount()>1) {
            last = Integer.parseInt(cMiles.getString(0));
            cMiles.moveToPrevious();
            previous = Integer.parseInt(cMiles.getString(0));
            cMiles.close();
            return last - previous;
        }else{
            cMiles.close();
            return 0;
        }
    }
    private String getLastDate(){
        long l=dbHelper.getLastDate();
        Date date=new Date(l*1000);
        SimpleDateFormat format=new SimpleDateFormat("MMM/dd/yyyy");
//        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return format.format(date);
    }
//    private int getMilesTotal(Cursor c){
//        int last=0,first = 0;
//        if(c!=null && c.moveToLast()) {
//            last = Integer.parseInt(c.getString(0));
//            c.moveToFirst();
//            first = Integer.parseInt(c.getString(0));
//            return last - first;
//        }else{
//            return 0;
//        }
//    }
//
//    private int getMilesSinceLast(Cursor c){
//        int last=0,previous = 0;
//        if(c!=null && c.moveToLast()) {
//            last = Integer.parseInt(c.getString(0));
//            c.moveToPrevious();
//            previous = Integer.parseInt(c.getString(0));
//            return last - previous;
//        }else{
//            return 0;
//        }
//    }

//    /**
//     * Deleteable methods for internal logging only
//     * @param
//     */
//
//    public void logData(){
//        Cursor c= dbHelper.getMilesColumn();
//
//        String names="";
//        for(String s: c.getColumnNames()){
//            names=names+s+", ";
//        }
//
//        Log.e("OverallStatsFragment", "OverallstatsFrag columns returned: " + names);
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
//        displayCurrentVehicleStats();
        getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks) this);
        getLoaderManager().restartLoader(1, null, (LoaderManager.LoaderCallbacks) this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL=null;
        switch (id) {
            case 0:
                CL = new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.a.b.mileagetracker/mpg_data"), null, null, null, null);
                break;
            case 1:
                SharedPreferences mSharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                if(mSharedPrefs.getString("currentVehicle",null)!=null) {
                    CL = new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.a.b.mileagetracker/vehicle"), null, mSharedPrefs.getString("currentVehicle","null"), null, null);
                }
                break;
        }
        return CL;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null) {
            switch (loader.getId()) {
                case 0: //pull last known MPG record
                    if (data.getCount() > 1) {
                        data.moveToFirst();
                        Double mpg = data.getDouble(0);
                        Log.e(TAG, "onLoadFInished mpg: " + mpg);
                        mpgSinceLast.setText(mpg > 0 ? "Most Recent: " + mpg + " mpg" : null);
                    } else {
                        mpgSinceLast.setText("Need two or more entries to compute MPG");
                    }
                    break;
                case 1: //calculate and display aggregate MPG since first record
                    if (data.getCount() > 1) {
                        data.moveToFirst();
                        int maxMiles = data.getInt(data.getColumnIndex(MySQLiteHelper.COLUMN_MILEAGE));
                        data.moveToLast();
                        int minMiles = data.getInt(data.getColumnIndex(MySQLiteHelper.COLUMN_MILEAGE));
                        int totalMiles = maxMiles - minMiles;

                        data.moveToFirst();
                        Double galsTotal = 0.0;
                        Double priceTotal = 0.0;
                        do {
                            galsTotal += data.getDouble(data.getColumnIndex(MySQLiteHelper.COLUMN_QUANTITY));
                            priceTotal += data.getDouble(data.getColumnIndex(MySQLiteHelper.COLUMN_PRICE));
                        } while (data.moveToNext());

                        mpgTotalView.setText("Total Average: " + String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(totalMiles / galsTotal)) + " mpg");
                        totalMilesTravelled.setText("Total miles tracked: " + NumberFormat.getNumberInstance(Locale.US).format(totalMiles));
                        totalAmountSpent.setText(NumberFormat.getCurrencyInstance().format(priceTotal) + " since " + getLastDate());
                    } else {
                        mpgTotalView.setText("");
                        totalMilesTravelled.setText("");
                        totalAmountSpent.setText(data.getCount() == 1 ? "First record created on: " + getLastDate() : "");
                    }
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
