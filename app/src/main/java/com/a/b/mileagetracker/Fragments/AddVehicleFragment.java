package com.a.b.mileagetracker.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.DialogInterfaces;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.DataAccess.SettingInterfaces;
import com.a.b.mileagetracker.Events.RefreshVehiclesEvent;
import com.a.b.mileagetracker.R;

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
    private MySQLiteHelper dbHelper;

    public static AddVehicleFragment newInstance() {
        return new AddVehicleFragment();
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
            try {
                int year=Integer.parseInt(addYear.getText().toString());
                if(year>1880&&year<2018||year<100){
                    String make=WordUtils.capitalizeFully(addMake.getText().toString());
                    String model=WordUtils.capitalizeFully(addModel.getText().toString());

                    make=make.trim();
                    model=model.trim();

                    if(make.length()>2&&model.length()>2&&make.length()<17&&model.length()<17){
                        String currentVehicle="\""+make+model+year+"\"";
                        currentVehicle=currentVehicle.replaceAll("\\s", "");

                        String currentVehicleGUI=year+" "+make+" "+model;

                        mSharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editPrefs=mSharedPrefs.edit();
                        editPrefs.putString("currentVehicle", currentVehicle);
                        editPrefs.putString("currentVehicleGUI", currentVehicleGUI);
                        editPrefs.commit();
                        try {
                            String cv=currentVehicle;
                            dbHelper.createVehicleTable(year, make, model, cv);
                            mListener.onAddVehicleDismiss();
                            EventBus.getDefault().postSticky(new RefreshVehiclesEvent());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"Invalid Car",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getActivity(),"Make and model need to be 3 to 16 characters long", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getActivity(),"Please enter a reasonable car year",Toast.LENGTH_LONG).show();
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(),"Invalid year, please try again",Toast.LENGTH_LONG).show();
            }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
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