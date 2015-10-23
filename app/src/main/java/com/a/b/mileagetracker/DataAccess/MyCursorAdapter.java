package com.a.b.mileagetracker.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.a.b.mileagetracker.R;

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

        vehicle.setText(cursor.getString(cursor.getColumnIndex("vehicle")));
        mileage.setText(cursor.getString(cursor.getColumnIndex("mileage")));
        gallons.setText(cursor.getString(cursor.getColumnIndex("quantity")));
        price.setText(cursor.getString(cursor.getColumnIndex("price")));
        date.setText(cursor.getString(cursor.getColumnIndex("date")));
        location.setText(cursor.getString(cursor.getColumnIndex("location")));


    }
}
