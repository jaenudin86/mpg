//package com.radicaldroids.mileage.DataAccess;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.CursorAdapter;
//import android.widget.TextView;
//
//import com.radicaldroids.mileage.Constants;
//import com.radicaldroids.mileage.R;
//
//
///**
// * Created by Andrew on 10/26/2015.
// */
//public class DropDownCursorAdapter extends CursorAdapter implements AdapterView.OnItemSelectedListener{
//
//    private LayoutInflater mInflater;
//    private Context context;
//    private Cursor cursor;
//
//    public DropDownCursorAdapter(Context context, Cursor c, int flags) {
//        super(context, c, flags);
//        this.context=context;
//        cursor=c;
//        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        return mInflater.inflate(R.layout.dropdown_listitem,parent,false);
//    }
//
//    @Override
//    public void bindView(View view, Context context, Cursor cursor) {
//        TextView vehicle=(TextView) view.findViewById(R.id.drop_text_view);
//        vehicle.setText(
//                cursor.getString(cursor.getColumnIndex("key_year"))+" "+
//                cursor.getString(cursor.getColumnIndex("key_make"))+" "+
//                cursor.getString(cursor.getColumnIndex("key_model")));
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        String currentVehicle=cursor.getString(cursor.getColumnIndex("key_table"));
////        Log.e("Selected!", "Selected!!!!!: current vehicle: "+currentVehicle);
//        SharedPreferences sharedPrefs=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=sharedPrefs.edit();
//        editor.putString(Constants.SHARED_PREFS_CURRENT_VEHICLE,currentVehicle).commit();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//    }
//}
