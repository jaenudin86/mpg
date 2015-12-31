package com.a.b.mileagetracker.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.DialogInterfaces;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.Events.RefreshHistoryListViewEvent;
import com.a.b.mileagetracker.R;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/20/2015.
 */
public class AddRecordDialogFrag extends DialogFragment implements View.OnClickListener{

    // Use this instance of the interface to deliver action events
    DialogInterfaces.DialogInterface mListener;
//    SQLDao mSqlDao = new DataDAOImplementation();
//    SQLDao mMySQLiteHelper;
    private MySQLiteHelper dbHelper;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText dateView;

//    public interface DialogInterface{
//        void onDialogAddEntryDismiss();
//        void onEditDate();
//        void selectCurrentCar(String make, String model, int year);
//    }

    public AddRecordDialogFrag(){}

    public static AddRecordDialogFrag newInstance(){
        return new AddRecordDialogFrag();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_record, null);

//        final Spinner carSelector=(Spinner) view.findViewById(R.id.vehicle_dropdown_spinner);
//        final Cursor c=dbHelper.getAllDataFromKeyTable();
//        DropDownCursorAdapter dropDownAdapt = new DropDownCursorAdapter(getActivity(), c, 0);
//        carSelector.setAdapter(dropDownAdapt);
//        carSelector.setOnItemSelectedListener(dropDownAdapt);

        final EditText location= (EditText) view.findViewById(R.id.station_location);
        final EditText mileage = (EditText) view.findViewById(R.id.mileage);
        final EditText gallons = (EditText) view.findViewById(R.id.gallons);
        final EditText price = (EditText) view.findViewById(R.id.price);

        SharedPreferences sharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final TextView vehicle = (TextView) view.findViewById(R.id.current_vehicle_add_record_frag);
        vehicle.setText(sharedPrefs.getString("currentVehicleGUI", "car"));

        dateView = (EditText) view.findViewById(R.id.date);
        dateView.setInputType(InputType.TYPE_NULL);
        dateView.setOnClickListener(this);

        dateFormatter=new SimpleDateFormat("MMM-dd-yyyy", Locale.US);
        dateView.setText(dateFormatter.format(new Date()));

        setDateField();

        Button bt=(Button) view.findViewById(R.id.add_new_car_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            convertDateFieldToInt();
            try {
//                    String carSelectorString= ((Cursor)carSelector.getSelectedItem()).getString(c.getColumnIndex("key_table"));
//                    Log.e("carselector spinner","csst: "+carSelectorString);
                DecimalFormat df3=new DecimalFormat("#.###");
                DecimalFormat df2=new DecimalFormat("0.00");

                dbHelper.addEntry(
//                            Integer.parseInt(mileage.getText().toString()),
                    (int) Math.round(Double.parseDouble(mileage.getText().toString())),
                    Double.valueOf(df3.format(Double.parseDouble(gallons.getText().toString()))),
                    Double.valueOf(df2.format(Double.parseDouble(price.getText().toString()))),
                    convertDateFieldToInt(),
                    WordUtils.capitalizeFully(location.getText().toString()));
//                        mListener.onDialogAddEntryDismiss();  //close dialog from Activity
                mListener.dismissDialogFragment(getTag());
                EventBus.getDefault().postSticky(new RefreshHistoryListViewEvent("refreshing"));
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(),"wrong number format",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }
    private void setDateField(){
            Calendar newCalendar = Calendar.getInstance();
            fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    dateView.setText(dateFormatter.format(newDate.getTime()));
                }
            },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        convertDateFieldToInt();
    }
    private long convertDateFieldToInt(){
        String dateString= dateView.getText().toString();
        SimpleDateFormat sdf=new SimpleDateFormat("MMM-dd-yyyy");
        try {
            Date date = sdf.parse(dateString);
//            Log.e("date","date: "+date.getTime());
            return date.getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onClick(View v) {
        if (v == dateView) {
            fromDatePickerDialog.show();
        }
    }
}
