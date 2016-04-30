package com.radicaldroids.mileage.DataAccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.radicaldroids.mileage.Constants;
import com.radicaldroids.mileage.Events.RefreshHistoryListViewEvent;
import com.radicaldroids.mileage.R;
//import com.radicaldroids.mileage.R;

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
            vehicle.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COLUMN_YEAR)) + " " +
                            cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COLUMN_MAKE)) + " " +
                            cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COLUMN_MODEL)));
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
        String currentVehicleGUI=cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COLUMN_YEAR))+" "
                +cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COLUMN_MAKE))+" "
                +cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COLUMN_MODEL));

        String currentVehicle=cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COLUMN_TABLE));

        SharedPreferences sharedPrefs=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPrefs.edit();
        editor.putString(Constants.SHARED_PREFS_CURRENT_VEHICLE_GUI, currentVehicleGUI);
        editor.putString(Constants.SHARED_PREFS_CURRENT_VEHICLE, currentVehicle).commit();

        EventBus.getDefault().post(new RefreshHistoryListViewEvent(currentVehicle));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
