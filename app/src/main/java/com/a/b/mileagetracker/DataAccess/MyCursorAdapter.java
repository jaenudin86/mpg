package com.a.b.mileagetracker.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.a.b.mileagetracker.R;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Andrew on 10/19/2015.
 */
public class MyCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public MyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return mInflater.inflate(R.layout.advanced_list_item, parent,false);
    }

    @Override
    //View returned from newView is passed as a first parameter to bindView
    public void bindView(View view, Context context, Cursor cursor) {
        TextView vehicle=(TextView) view.findViewById(R.id.vehicle_list_item);
        TextView mileage=(TextView) view.findViewById(R.id.mileage_list_item);
        TextView gallons=(TextView) view.findViewById(R.id.gallons_list_item);
        TextView price=(TextView) view.findViewById(R.id.price_list_item);
        TextView date=(TextView) view.findViewById(R.id.date_list_item);
        TextView location= (TextView) view.findViewById(R.id.location_list_item);

        vehicle.setText("Vehicle: "+(cursor.getString(cursor.getColumnIndex("vehicle"))));
        mileage.setText("Mileage: "+cursor.getString(cursor.getColumnIndex("mileage")));
        gallons.setText("Quantity added: "+cursor.getString(cursor.getColumnIndex("quantity"))+" gallons");
        price.setText("Price: $"+cursor.getString(cursor.getColumnIndex("price")));

//        date.setText(cursor.getString(cursor.getColumnIndex("date")));

        date.setText(convertTime(cursor.getInt(cursor.getColumnIndex("date"))));

//        date.setText("test");
        location.setText("Station: "+cursor.getString(cursor.getColumnIndex("location")));

    }
    private String convertTime(long l){
//
//        Log.e("i=","date i= "+i);//
//        Date e = new Date(Long.parseLong(i+"")*1000);//
//        return e;


//        String seconds=Long.toString(l*1000);
        Date dateInSeconds=new Date(l*1000);

        Log.e("date","date: "+dateInSeconds+" seconds: "+l*1000);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
//        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = sdf.format(dateInSeconds);
        return formatted;


//        Date date1=null;
//        try {
//            date1 = sdf.parse(s);
//            Log.e("parse","parse:  "+date1);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return date1.toString();




//        Format formatter = new SimpleDateFormat("MMM-dd-yyyy");
//        String s = formatter.format(i);
//        return s;


//        Date date2 = sdf.parse(1995-01-01);
//        value2=date.getTime();


    }

}
