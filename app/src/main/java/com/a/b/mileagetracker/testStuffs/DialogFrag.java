package com.a.b.mileagetracker.testStuffs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.DataDAOImplementation;
import com.a.b.mileagetracker.DataAccess.MyCursorAdapter;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.DataAccess.SQLDao;
import com.a.b.mileagetracker.R;

/**
 * Created by Andrew on 10/20/2015.
 */
public class DialogFrag extends DialogFragment {

    // Use this instance of the interface to deliver action events
    DialogInterface mListener;
//    SQLDao mSqlDao = new DataDAOImplementation();
//    SQLDao mMySQLiteHelper;
    private MySQLiteHelper dbHelper;

    public interface DialogInterface{
        public void onDialogAddVehicle();
    }

    public DialogFrag(){}

    public static DialogFrag newInstance(){
        return new DialogFrag();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        mMySQLiteHelper = new MySQLiteHelper(getActivity().getApplicationContext());
        dbHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.car_selector_dialog, null);
        final EditText vehicle = (EditText) view.findViewById(R.id.vehicle);
        final EditText mileage = (EditText) view.findViewById(R.id.mileage);
        final EditText gallons = (EditText) view.findViewById(R.id.gallons);
        final EditText price = (EditText) view.findViewById(R.id.price);
        final EditText date = (EditText) view.findViewById(R.id.date);
        Button bt=(Button) view.findViewById(R.id.add_new_car_button);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbHelper.addEntry(
                            vehicle.getText().toString(),
                            Integer.parseInt(mileage.getText().toString()),
                            Double.parseDouble(gallons.getText().toString()),
                            Double.parseDouble(price.getText().toString()),
                            Integer.parseInt(date.getText().toString()));

                    mListener.onDialogAddVehicle();
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(),"wrong number format",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

//        builder.setPositiveButton("yes", null);
//        builder.setNegativeButton("no",null);

//        return new AlertDialog.Builder(getActivity())
//                .setTitle("yeeeppps")
//                .setMessage("noooppppee")
////                .setPositiveButton("click me!",)
//                .create();
        return builder.create();
    }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogInterface) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
