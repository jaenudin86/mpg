package com.a.b.mileagetracker.Fragments;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.DialogInterfaces;
import com.a.b.mileagetracker.DataAccess.HistoryCursorAdapter;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.R;
import com.a.b.mileagetracker.Model.MessageEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/14/2015.
 */
public class AllHistoryFragment extends Fragment {

    private MySQLiteHelper mDBHelper;
    private ListView mListView;
    private TextView header;
    private DialogInterfaces.DialogInterface mListener;
    public static HistoryCursorAdapter mHistoryCursorAdapter;

    public AllHistoryFragment(){
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDBHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.all_data_listview_fragment, container, false);
         header=(TextView) view.findViewById(R.id.all_data_listview_title);
//        Spinner carSpinner=(Spinner) view.findViewById(R.id.dropdown_spinner_all_data_frag);

        SharedPreferences mSharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        header.setText("All entries for: "+(mSharedPrefs.getString("currentVehicle","not found")));

//        final Cursor c=mDBHelper.getAllDataFromKeyTable();
//        DropDownCursorAdapter dropDownAdapt = new DropDownCursorAdapter(getActivity(), c, 0);
//        carSpinner.setAdapter(dropDownAdapt);
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
//        carSpinner.setOnItemSelectedListener(dropDownAdapt);

        mDBHelper = MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        Cursor cursor=mDBHelper.getAllData();
        cursor.moveToFirst();

        mListView=(ListView) view.findViewById(R.id.listview);
        mHistoryCursorAdapter = new HistoryCursorAdapter(getActivity(), cursor,0);
        mListView.setAdapter(mHistoryCursorAdapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mListener.openEditVehicleEntryFragment();

                Log.e("long click", "long clicked: " + view + ", " + position + ", " + id);
                return true;
            }
        });

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

        mDBHelper = MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        Cursor cursor=mDBHelper.getAllData();
        mHistoryCursorAdapter.changeCursor(cursor);
        mHistoryCursorAdapter.notifyDataSetChanged();

        header.setText("All entries for: "+event);
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
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogInterfaces.DialogInterface) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
