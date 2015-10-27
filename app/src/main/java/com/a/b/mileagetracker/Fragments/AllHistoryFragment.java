package com.a.b.mileagetracker.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.DropDownCursorAdapter;
import com.a.b.mileagetracker.DataAccess.MyCursorAdapter;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.R;
import com.a.b.mileagetracker.testStuffs.MessageEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/14/2015.
 */
public class AllHistoryFragment extends Fragment {

    private MySQLiteHelper dbHelper;
    private ListView listview;
    public static MyCursorAdapter advancedCursorAdapter;

    public AllHistoryFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.all_data_listview_fragment, container, false);
        TextView header=(TextView) view.findViewById(R.id.all_data_listview_title);
        Spinner carSpinner=(Spinner) view.findViewById(R.id.dropdown_spinner_all_data_frag);

        SharedPreferences mSharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        header.setText("All entries for: "+(mSharedPrefs.getString("currentVehicle","not found")));

        final Cursor c=dbHelper.getAllDataFromKeyTable();
        DropDownCursorAdapter dropDownAdapt = new DropDownCursorAdapter(getActivity(), c, 0);
        carSpinner.setAdapter(dropDownAdapt);
//        carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("selected","selected: "+parent+", "+view+", "+position+", "+id);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        carSpinner.setOnItemSelectedListener(dropDownAdapt);

        dbHelper = MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        Cursor cursor=dbHelper.getAllData();
        cursor.moveToFirst();

        listview=(ListView) view.findViewById(R.id.listview);
        advancedCursorAdapter = new MyCursorAdapter(getActivity(), cursor,0);
        listview.setAdapter(advancedCursorAdapter);
/**
 * SimpleCursorAdapter method of creating view
 */
//        listview=(ListView) view.findViewById(R.id.listview);
//        String[] columns={cursor.getColumnName(1)};
//        int[] displayViews = {R.id.list_text};
//        cursorAdapter=new SimpleCursorAdapter(getActivity(),R.layout.list_item,cursor,columns,displayViews,0);
//        listview.setAdapter(cursorAdapter);
////        listview.setOnItemClickListener();
        return view;
    }

    public void onEvent(MessageEvent event){
        Log.e("onEvent", "onEvent received");
        Toast.makeText(getActivity(),event.message,Toast.LENGTH_LONG).show();
//        String insert_query="INSERT INTO fillupTable (location) "+"VALUES ('bp')";
//        db.execSQL(insert_query);
//        Cursor c =db.rawQuery("SELECT _id, location FROM fillupTable",null);
//        cursorAdapter.changeCursor(c);
    }

//    public void onEvent(SomeOtherEvent event){
//        Log.e("","something else received");
//    }
//    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


}