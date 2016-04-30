package com.radicaldroids.mileage.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.radicaldroids.mileagetracker.DataAccess.DialogInterfaces;
//import com.radicaldroids.mileagetracker.DataAccess.SQLiteHelper;
//import com.radicaldroids.mileagetracker.DataAccess.SettingInterfaces;
//import com.radicaldroids.mileagetracker.Events.RefreshVehiclesEvent;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.radicaldroids.mileage.Constants;
import com.radicaldroids.mileage.DataAccess.SQLiteHelper;
import com.radicaldroids.mileage.Events.RefreshVehiclesEvent;
import com.radicaldroids.mileage.MyApplication;
import com.radicaldroids.mileage.R;


import org.apache.commons.lang3.text.WordUtils;

import de.greenrobot.event.EventBus;


/**
 * Created by Andrew on 10/25/2015.
 */
public class AddVehicleFragment extends DialogFragment {
    public interface AddVehicle{
        void onAddVehicle();
        void onAddVehicleDismiss();
    }

    AddVehicleFragment.AddVehicle mListener;
    SharedPreferences mSharedPrefs;
    private SQLiteHelper dbHelper;
    private String TAG="AddVehicleFragment";
    private Tracker mTracker;

    public static AddVehicleFragment newInstance() {
        return new AddVehicleFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dbHelper = SQLiteHelper.getInstance(getActivity().getApplicationContext());

        MyApplication application=(MyApplication) getActivity().getApplication();
        mTracker=application.getTracker();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_vehicle, null);
        final EditText addMake = (EditText) view.findViewById(R.id.add_make);
        final EditText addModel = (EditText) view.findViewById(R.id.add_model);
        final EditText addYear = (EditText) view.findViewById(R.id.add_year);
        Button enterButton = (Button) view.findViewById(R.id.add_new_car_button);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                int year=Integer.parseInt(addYear.getText().toString());
                //verify that car's year is reasonable, can be entered as 4 digit or 2 digit year
                if(year>1880&&year<2019||year<100){
                    String make=WordUtils.capitalizeFully(addMake.getText().toString());
                    String model=WordUtils.capitalizeFully(addModel.getText().toString());

                    make=make.trim();
                    model=model.trim();

                    if(make.length()>2&&model.length()>2&&make.length()<17&&model.length()<17){
                        String currentVehicle="\""+make+model+year+"\"";
                        currentVehicle=currentVehicle.replaceAll("\\s", "");

                        String currentVehicleGUI=year+" "+make+" "+model;

                        mSharedPrefs=getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editPrefs=mSharedPrefs.edit();
                        editPrefs.putString(Constants.SHARED_PREFS_CURRENT_VEHICLE, currentVehicle);
                        editPrefs.putString(Constants.SHARED_PREFS_CURRENT_VEHICLE_GUI, currentVehicleGUI);
                        editPrefs.commit();
                        try {
                            String cv=currentVehicle;
                            Log.e(TAG,"year: "+year+" make: "+make+" model: "+model+" currentVehicle: "+cv);
                            dbHelper.createVehicleTable(year, make, model, cv);
                            mListener.onAddVehicleDismiss();
                            EventBus.getDefault().postSticky(new RefreshVehiclesEvent());
                            sendAnalytic();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),R.string.invalid_car,Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getActivity(),R.string.reasonable_length, Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getActivity(),R.string.reasonable_year,Toast.LENGTH_LONG).show();
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(),R.string.invalid_year,Toast.LENGTH_LONG).show();
            }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    private void sendAnalytic(){
        mTracker.setScreenName(getString(R.string.vehicle_added_analytic_tag));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
//            mDialogInterface=(DialogInterfaces.DialogInterface) activity.;
            mListener = (AddVehicleFragment.AddVehicle) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}