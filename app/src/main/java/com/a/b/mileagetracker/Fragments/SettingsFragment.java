package com.a.b.mileagetracker.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.a.b.mileagetracker.DataAccess.DialogInterfaces;
import com.a.b.mileagetracker.R;

import java.util.zip.Inflater;

/**
 * Created by Andrew on 10/30/2015.
 */
public class SettingsFragment extends DialogFragment implements View.OnClickListener {
    DialogInterfaces.DialogInterface mListener;
    public SettingsFragment() {
    }
    public static SettingsFragment newInstance(){
        SettingsFragment settingsFragment=new SettingsFragment();
        return settingsFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.settings_fragment,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton("Edit",null).setNegativeButton("Delete",null).setNeutralButton("cancel", null);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.settings_fragment,container,false);
        Button addEditVehicle=(Button) view.findViewById(R.id.add_edit_vehicle);
        addEditVehicle.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.add_edit_vehicle){
//            mListener.onDialogAddVehicle();
            mListener.openVehicleListFragment();
            Log.e("button pushed","button pushed");
        }
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
