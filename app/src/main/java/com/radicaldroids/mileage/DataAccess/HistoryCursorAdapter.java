package com.radicaldroids.mileage.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.radicaldroids.mileage.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andrew on 10/19/2015.
 */
public class HistoryCursorAdapter extends CursorAdapter{

    private DialogInterfaces.DialogInterface mListener;
    private LayoutInflater mInflater;
    private Cursor cursor;

    public HistoryCursorAdapter(Context context, Cursor c, int flags) {
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

        TextView mileage=(TextView) view.findViewById(R.id.mileage_list_item);
        TextView gallons=(TextView) view.findViewById(R.id.gallons_list_item);
        TextView price=(TextView) view.findViewById(R.id.price_list_item);
        TextView date=(TextView) view.findViewById(R.id.date_list_item);
        TextView location= (TextView) view.findViewById(R.id.location_list_item);
        TextView mpg=(TextView) view.findViewById(R.id.mpg_list_item);

        NumberFormat money=NumberFormat.getCurrencyInstance();

        mileage.setText("Odometer: "+NumberFormat.getIntegerInstance().format(cursor.getInt(cursor.getColumnIndex("odometer"))));
        gallons.setText("Quantity added: "+cursor.getString(cursor.getColumnIndex("quantity"))+" gallons");
        price.setText("Price: "+money.format(Double.parseDouble(cursor.getString(cursor.getColumnIndex("price")))));

        date.setText(convertTime(cursor.getInt(cursor.getColumnIndex("date"))));

        location.setText("Station: "+cursor.getString(cursor.getColumnIndex("location")));
        Double mpgValue=cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_MPG));
        mpg.setText("MPG: "+ (mpgValue==0.0?"--":mpgValue));
    }
    @Override
    public void changeCursor(Cursor c) {
        cursor=c;
        super.changeCursor(cursor);
    }

    private String convertTime(long l){
        Date dateInSeconds=new Date(l*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
//        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = sdf.format(dateInSeconds);
        return formatted;
    }
}
