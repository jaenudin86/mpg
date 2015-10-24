package com.a.b.mileagetracker.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.R;

import java.text.DecimalFormat;

/**
 * Created by Andrew on 10/22/2015.
 */
public class OverallStatsFragment extends Fragment {

    public MySQLiteHelper dbHelper;
    private int mMilesRecent=0;
    private int mMilesTotal=0;

    public OverallStatsFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.overall_stats_fragment, container, false);
        TextView mpgSinceLast=(TextView)view.findViewById(R.id.perfm_mpg_since_last);
        TextView mpgTotal =(TextView) view.findViewById(R.id.perfm_mpg_overall);
        TextView milesTravelled=(TextView) view.findViewById(R.id.perfm_miles_travelled);
        TextView totalMilesTravelled =(TextView) view.findViewById(R.id.perfm_miles_travelled_total);
        TextView totalAmountSpent=(TextView) view.findViewById(R.id.perfm_spent_total);
        TextView conclusion = (TextView) view.findViewById(R.id.perfm_conclusion);

        Cursor milesCursor= dbHelper.getMilesColumn();
        mMilesRecent=getMilesSinceLast();
        mMilesTotal=getMilesTotal();


        mpgSinceLast.setText("Most recent: "+getMpgRecent()+" mpg");
        mpgTotal.setText("Total: "+getMpgTotal()+" mpg");
        milesTravelled.setText("Miles since last record: " + mMilesRecent);
        totalMilesTravelled.setText("Total miles tracked: "+mMilesTotal);
        totalAmountSpent.setText("$ "+dbHelper.getTotalAmountSpent()+" since 10/1/15");

        logData(milesCursor);
        return view;
    }
    private Double getMpgRecent(){
        Double gallons;
        Cursor cQuant=dbHelper.getQuantityColumn();
        DecimalFormat df=new DecimalFormat("#.###");
        if(cQuant!=null && cQuant.moveToLast()){
            gallons=Double.parseDouble(cQuant.getString(0));
            return Double.parseDouble(df.format(mMilesRecent / gallons));
        }else{
            return 0.0;
        }
    }
    private Double getMpgTotal(){
        Double gallons;
        DecimalFormat df=new DecimalFormat("#.###");
//        if(c!=null && c.moveToFirst()){
            Cursor galCur=dbHelper.getSumGallons();
            galCur.moveToFirst();

            gallons=Double.parseDouble(galCur.getString(0));
            return Double.parseDouble(df.format(mMilesTotal/gallons));
//        }else{
//            return 0.0;
//        }
    }

    private int getMilesTotal(){
        int last,first;
        Cursor cMiles=dbHelper.getMilesColumn();
        if(cMiles!=null && cMiles.moveToLast()) {
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
        if(cMiles!=null && cMiles.moveToLast()) {
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

    /**
     * Deleteable methods for internal logging only
     * @param c
     */

    public void logData(Cursor c){
        String names="";
        for(String s: c.getColumnNames()){
            names=names+s+", ";
        }

        Log.e("OverallStatsFragment", "OverallstatsFrag columns returned: " + names);

        if(c!=null){
            if(c.moveToFirst()){
                Log.e("row","row by row (first): "+c.getString(0));
            }
            if(c.moveToLast()){
                Log.e("row","row by row (last): "+c.getString(0));
            }
            c.moveToFirst();
            int n=0;
            while(c.moveToNext()){
                Log.e("row","row by row: "+n+", "+c.getString(0));
                n++;
            }
            c.moveToLast();
            Log.e("row", "row by row (last): " + c.getString(0));
            c.moveToPrevious();
            Log.e("row", "row by row (previous): " + c.getString(0));
        }
//        c.close();
    }
}
