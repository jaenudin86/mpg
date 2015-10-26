package com.a.b.mileagetracker.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.R;


/**
 * Created by Andrew on 10/25/2015.
 */
public class AddVehicleDialogFrag extends DialogFragment {
    private MySQLiteHelper dbHelper;

    public static AddVehicleDialogFrag newInstance(){
        return new AddVehicleDialogFrag();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dbHelper= MySQLiteHelper.getInstance(getActivity().getApplicationContext());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_vehicle, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();

    }
}
