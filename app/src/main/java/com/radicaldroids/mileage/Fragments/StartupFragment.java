package com.radicaldroids.mileage.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.radicaldroids.mileage.R;

/**
 * Created by Andrew on 12/22/2015.
 */
public class StartupFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{
//    private Button mEmptyButton;
    private TextView mInitMessage;
    AddVehicleFragment.AddVehicle mListener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.startup_fragment, container, false);
//        mEmptyButton = (Button) view.findViewById(R.id.empty_history_button);
        mInitMessage=(TextView) view.findViewById(R.id.init_message);
//        mEmptyButton.setOnClickListener(this);
        setScreen();
        return view;
    }

    @Override
    public void onClick(View v) {
        mListener.onAddVehicle();
    }
//    public void onEvent(RefreshHistoryListViewEvent event){
    public void setScreen(){
        SharedPreferences mSharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String currentVehicle=mSharedPrefs.getString("currentVehicle",null);
        if(currentVehicle==null){
//            mEmptyButton.setText(R.string.get_started);
//            mEmptyButton.setVisibility(View.VISIBLE);
            mInitMessage.setText(R.string.init_message_no_vehicles);
            mInitMessage.setVisibility(View.VISIBLE);
        }else{
            Bundle extra = new Bundle();
            extra.putString("vehicle", currentVehicle);
            getLoaderManager().initLoader(0, extra, (LoaderManager.LoaderCallbacks) this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL=null;
        CL=new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.radicaldroids.mileage/vehicle"), null, args.getString("vehicle"), null, null);
        return CL;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount()<1){
//            mEmptyButton.setText(R.string.add_record_title);
//            mEmptyButton.setVisibility(View.VISIBLE);
            mInitMessage.setText(R.string.init_message_no_data);
            mInitMessage.setVisibility(View.VISIBLE);
        }else{
//            mEmptyButton.setVisibility(View.GONE);
            mInitMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host

            mListener = (AddVehicleFragment.AddVehicle) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
