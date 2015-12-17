package com.a.b.mileagetracker.Fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.a.b.mileagetracker.DataAccess.DialogInterfaces;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.DataAccess.SQLDao;
import com.a.b.mileagetracker.DataAccess.VehicleCursorAdapter;
import com.a.b.mileagetracker.Events.RefreshVehiclesEvent;
import com.a.b.mileagetracker.R;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 12/8/2015.
 */
public class VehicleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    DialogInterfaces.DialogInterface mListener;
    ListView mListView;
    VehicleCursorAdapter vehicleCurAdpt;
    String TAG="VehicleListFragment";

    public static VehicleListFragment newInstance(){
        VehicleListFragment vehicleListFragment=new VehicleListFragment();
        return vehicleListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);   //tends to cause problems on rotate?

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState==null) {
            getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks) this);
        }else{
            getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks) this);
        }
        View view = inflater.inflate(R.layout.vehicle_listview_fragment, container, false);
        mListView=(ListView) view.findViewById(R.id.vehicle_listview);
        Button mAdd=(Button) view.findViewById(R.id.add_new_vehicle);
        mAdd.setOnClickListener(this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL=null;
        switch (id){
            case 0:
                CL=new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.a.b.mileagetracker/key_table"), null, null, null, null);
            break;
        }
        return CL;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        vehicleCurAdpt=new VehicleCursorAdapter(getActivity(),data,0);
        mListView.setAdapter(vehicleCurAdpt);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                Log.e(TAG, "clicked position: " + position + ", long id: " + id);
                TextView flag = (TextView) view.findViewById(R.id.text_delete_flag);

                if (flag.getVisibility() == View.VISIBLE) {
                    flag.setVisibility(View.INVISIBLE);
                } else {
                    flag.setVisibility(View.VISIBLE);
//                    mListView.getItemAtPosition(position)
                    flag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            data.moveToPosition((int) id - 1);
                            data.moveToPosition(position);
//                            Log.e(TAG, "inner click on position: " + position + ", id: " + data.getString(data.getColumnIndex(MySQLiteHelper.KEY_COLUMN_TABLE)));
                            Log.e(TAG, "index: " + data.getColumnIndex(MySQLiteHelper.KEY_COLUMN_TABLE));
                            Log.e(TAG, "data.getstring(index): " + data.getColumnName(data.getColumnIndex(MySQLiteHelper.KEY_COLUMN_TABLE)));

                            deleteVehicle(data.getString(data.getColumnIndex(MySQLiteHelper.KEY_COLUMN_TABLE)));
                        }
                    });
                }

                return true;
            }
        });
    }
    public void deleteVehicle(String vehicle){
        SharedPreferences sharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String currentVehicle=sharedPrefs.getString("currentVehicle", "null");
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if(currentVehicle.compareToIgnoreCase(vehicle)==0){
            editor.putString("currentVehicle", null).apply();

        }
        ContentResolver cr=getActivity().getContentResolver();
        int del=cr.delete(Uri.parse("content://com.a.b.mileagetracker/delete_vehicle"),vehicle,null);
        getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks) this);
        mListener.updateToolBarView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.add_new_vehicle){
            mListener.onDialogAddVehicle();
        }
    }

    public void onEvent(RefreshVehiclesEvent event){
        Log.e(TAG, "refresh event caught");
        getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks) this);
//        vehicleCurAdpt.notifyDataSetChanged();

    }
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
