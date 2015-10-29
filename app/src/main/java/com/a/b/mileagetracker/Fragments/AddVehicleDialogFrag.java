package com.a.b.mileagetracker.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.a.b.mileagetracker.DataAccess.DialogInterfaces;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.DataAccess.SQLDao;
import com.a.b.mileagetracker.R;


/**
 * Created by Andrew on 10/25/2015.
 */
public class AddVehicleDialogFrag extends DialogFragment {
//    AddRecordDialogFrag.DialogInterface mListener;
    DialogInterfaces.DialogInterface mListener;
    SharedPreferences mSharedPrefs;
    private MySQLiteHelper dbHelper;

    public static AddVehicleDialogFrag newInstance() {
        return new AddVehicleDialogFrag();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dbHelper = MySQLiteHelper.getInstance(getActivity().getApplicationContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_vehicle, null);
        final EditText addMake = (EditText) view.findViewById(R.id.add_make);
        final EditText addModel = (EditText) view.findViewById(R.id.add_model);
        final EditText addYear = (EditText) view.findViewById(R.id.add_year);
        Button enterButton = (Button) view.findViewById(R.id.add_new_car_button);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentVehicle=
                        addMake.getText().toString()+
                        addModel.getText().toString()+
                        Integer.parseInt(addYear.getText().toString());
                currentVehicle=currentVehicle.replaceAll("\\s", "");

                String currentVehicleGUI=Integer.parseInt(addYear.getText().toString())+" "
                        +addMake.getText().toString()+" "
                        + addModel.getText().toString();

                mSharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editPrefs=mSharedPrefs.edit();
                editPrefs.putString("currentVehicle",currentVehicle);
                editPrefs.putString("currentVehicleGUI", currentVehicleGUI);
                editPrefs.commit();

                dbHelper.createVehicleTable(Integer.parseInt(addYear.getText().toString()),
                        addMake.getText().toString(),
                        addModel.getText().toString(),
                        currentVehicle);
                mListener.onDialogAddVehicleDismiss();

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();

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
