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
import com.radicaldroids.mileage.Constants;
import com.radicaldroids.mileage.DataAccess.DataProvider;
import com.radicaldroids.mileage.DataAccess.SQLiteHelper;
import com.radicaldroids.mileage.DataAccess.VehicleCursorAdapter;
import com.radicaldroids.mileage.Events.RefreshVehiclesEvent;
import com.radicaldroids.mileage.MyApplication;
import com.radicaldroids.mileage.R;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 12/8/2015.
 */
public class VehicleListFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
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

        setRetainInstance(true);

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
                    CL=new CursorLoader(getActivity().getApplicationContext(), Uri.parse(DataProvider.BASE_CONTENT_URI +"/key_table"), null, null, null, null);
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
                                        deleteVehicle(data.getString(data.getColumnIndex(SQLiteHelper.KEY_COLUMN_TABLE)));
                                        mListener.openVehicleListDismiss();
                                    }
                                })
                                .setNegativeButton(R.string.alert_dialog_no, null)
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
        SharedPreferences sharedPrefs=getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        String currentVehicle=sharedPrefs.getString(Constants.SHARED_PREFS_CURRENT_VEHICLE, "null");

        if(currentVehicle.compareToIgnoreCase(vehicle)==0){
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(Constants.SHARED_PREFS_CURRENT_VEHICLE, null).apply();
        }

        getActivity().getContentResolver().delete(Uri.parse(DataProvider.BASE_CONTENT_URI +"/delete_vehicle"),vehicle,null);
        getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks) this);

        sendAnalytic();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onEvent(RefreshVehiclesEvent event){
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
        mTracker.setScreenName(getString(R.string.deleted_vehicle_analytic_tag));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (VehicleList) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
