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

    private LayoutInflater mInflator;

    public MyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflator=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return mInflator.inflate(R.layout.advanced_list_item, parent,false);
    }

    @Override
    //View returned from newView is passed as a first parameter to bindView
    public void bindView(View view, Context context, Cursor cursor) {
        TextView content=(TextView) view.findViewById(R.id.list_text);
        content.setText(cursor.getString(cursor.getColumnIndex("location")));

    }
}
