package com.radicaldroids.mileage.Fragments;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.radicaldroids.mileage.DataAccess.MySQLiteHelper;
import com.radicaldroids.mileage.DataAccess.VehicleCursorAdapter;
import com.radicaldroids.mileage.Events.RefreshVehiclesEvent;
import com.radicaldroids.mileage.MyApplication;
import com.radicaldroids.mileage.R;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 12/8/2015.
 */
public class VehicleListFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    public interface VehicleList {
        void openVehicleListDismiss();
    }
    VehicleList mListener;
    ListView mListView;
    VehicleCursorAdapter vehicleCurAdpt;
    String TAG="VehicleListFragment";
    private Tracker mTracker;

    public static VehicleListFragment newInstance(){
        VehicleListFragment vehicleListFragment=new VehicleListFragment();
        return vehicleListFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater=getActivity().getLayoutInflater();
        final View view=inflater.inflate(R.layout.vehicle_listview_fragment,null);
        mListView=(ListView) view.findViewById(R.id.vehicle_listview);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication application=(MyApplication) getActivity().getApplication();
        mTracker=application.getTracker();

        setRetainInstance(true);   //tends to cause problems on rotate?

        if(savedInstanceState==null) {
            getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks) this);
        }else{
            getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks) this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader CL=null;
            switch (id){
                case 0:
                    CL=new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.radicaldroids.mileage/key_table"), null, null, null, null);
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
//                Log.e(TAG, "clicked position: " + position + ", long id: " + id);
                TextView flag = (TextView) view.findViewById(R.id.text_delete_flag);

                if (flag.getVisibility() == View.VISIBLE) {
                    flag.setVisibility(View.INVISIBLE);
                } else {
                    flag.setVisibility(View.VISIBLE);
//                    mListView.getItemAtPosition(position)
                    flag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.alert_dialog_delete_vehicle_title)
                                .setMessage(R.string.alert_dialog_delete_vehicle_message)
                                .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        data.moveToPosition(position);
                                        deleteVehicle(data.getString(data.getColumnIndex(MySQLiteHelper.KEY_COLUMN_TABLE)));
                                        mListener.openVehicleListDismiss();
                                    }
                                })
                                .setNegativeButton(R.string.alert_dialog_no, null)
//                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                .setIcon(R.drawable.alert_48x48)
                                .show();
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
        int del=cr.delete(Uri.parse("content://com.radicaldroids.mileage/delete_vehicle"),vehicle,null);
        getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks) this);
//        mListener.updateSharedPrefsVehicles();
        //TODO THIS!!!!
//        mListener.updateToolBarView();
        sendAnalytic();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    @Override
    public void onClick(View v) {
//        int id=v.getId();
//        if(id==R.id.add_new_vehicle){
//            mListener.onAddVehicle();
//        }
    }

    public void onEvent(RefreshVehiclesEvent event){
//        Log.e(TAG, "RefreshVehiclesEvent event caught");
        getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks) this);
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
    public void onResume() {
        super.onResume();
    }
    private void sendAnalytic(){
        mTracker.setScreenName("Deleted Vehicle");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (VehicleList) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
