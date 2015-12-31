package com.a.b.mileagetracker.DataAccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.a.b.mileagetracker.Events.RefreshHistoryListViewEvent;
import com.a.b.mileagetracker.R;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/27/2015.
 */
public class ToolBarCursorAdapter extends CursorAdapter implements AdapterView.OnItemSelectedListener{
    private Context context;
    private Cursor cursor;
    private LayoutInflater mInflater;
    String TAG="ToolBarCursorAdapter";

    public ToolBarCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        this.cursor=c;
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        Log.e(TAG,"newView");
        return mInflater.inflate(R.layout.dropdown_listitem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView vehicle = (TextView) view.findViewById(R.id.drop_text_view);
//        Log.e(TAG, "bindView");
        if(cursor.getCount()>0) {
            vehicle.setText(
                    cursor.getString(cursor.getColumnIndex("key_year")) + " " +
                            cursor.getString(cursor.getColumnIndex("key_make")) + " " +
                            cursor.getString(cursor.getColumnIndex("key_model")));
        }else{
            vehicle.setText("No Vehicle");
        }
    }

    @Override
    public void changeCursor(Cursor c) {
        cursor=c;
        super.changeCursor(cursor);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String currentVehicleGUI=cursor.getString(cursor.getColumnIndex("key_year"))+" "+cursor.getString(cursor.getColumnIndex("key_make"))+" "+cursor.getString(cursor.getColumnIndex("key_model"));
        String currentVehicle=cursor.getString(cursor.getColumnIndex("key_table"));
        SharedPreferences sharedPrefs=context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPrefs.edit();
        editor.putString("currentVehicleGUI", currentVehicleGUI);
        editor.putString("currentVehicle",currentVehicle).commit();
        EventBus.getDefault().post(new RefreshHistoryListViewEvent(currentVehicle));

//        mSharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editPrefs=mSharedPrefs.edit();
//        editPrefs.putString("currentVehicle",currentVehicle);
//        editPrefs.putString("currentVehicleGUI", currentVehicleGUI);
//        editPrefs.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
