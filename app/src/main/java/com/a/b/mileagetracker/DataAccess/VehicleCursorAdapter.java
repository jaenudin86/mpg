package com.a.b.mileagetracker.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a.b.mileagetracker.R;

/**
 * Created by Andrew on 12/8/2015.
 */
public class VehicleCursorAdapter extends CursorAdapter {

    public VehicleCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
//        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater mInflater;
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return mInflater.inflate(R.layout.single_vehicle_layout,parent,false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView vehicle=(TextView) view.findViewById(R.id.vehicle);
        vehicle.setText("vehicle: "+cursor.getString(cursor.getColumnIndex(MySQLiteHelper.KEY_COLUMN_YEAR)));
    }
}
