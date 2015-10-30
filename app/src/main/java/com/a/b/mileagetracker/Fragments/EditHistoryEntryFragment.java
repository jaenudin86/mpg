package com.a.b.mileagetracker.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.a.b.mileagetracker.Model.EditHistoryEvent;
import com.a.b.mileagetracker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/20/2015.
 */
public class EditHistoryEntryFragment extends DialogFragment implements View.OnClickListener{

    DialogInterfaces.DialogInterface mListener;
    private MySQLiteHelper dbHelper;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText dateView;
    private EditText location;
    private EditText mileage;
    private EditText gallons;
    private EditText price;
    private Button deleteButton;
    private long mId;

    public EditHistoryEntryFragment(){
    }

//    public static EditHistoryEntryFragment newInstance(){
//        return new EditHistoryEntryFragment();
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dbHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.edit_record, null);

        location= (EditText) view.findViewById(R.id.edit_station_location);
        mileage = (EditText) view.findViewById(R.id.edit_mileage);
        gallons = (EditText) view.findViewById(R.id.edit_gallons);
        price = (EditText) view.findViewById(R.id.edit_price);
        dateView = (EditText) view.findViewById(R.id.edit_date);

        SharedPreferences sharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final TextView vehicle = (TextView) view.findViewById(R.id.current_vehicle_edit_record_frag);
        vehicle.setText(sharedPrefs.getString("currentVehicleGUI", "car"));

        dateView.setInputType(InputType.TYPE_NULL);
        dateView.setOnClickListener(this);

        dateFormatter=new SimpleDateFormat("MMM-dd-yyyy", Locale.US);

        setDateField();

        Button editButton=(Button) view.findViewById(R.id.edit_details_submit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                convertDateFieldToInt();
                try {
                    dbHelper.addEntry(
                            Integer.parseInt(mileage.getText().toString()),
                            Double.parseDouble(gallons.getText().toString()),
                            Double.parseDouble(price.getText().toString()),
                            convertDateFieldToInt(),
                            location.getText().toString());
                    mListener.dismissDialogFragment(getTag());
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(),"wrong number format",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        deleteButton=(Button) view.findViewById(R.id.delete_details_button);
        deleteButton.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(EditHistoryEvent editHistoryEvent){
        Cursor c=editHistoryEvent.mC;
        int position=editHistoryEvent.mPosition;
        mId = editHistoryEvent.mId;
        Log.e("event clicked"," clickedclickedclicked position==> "+editHistoryEvent.mPosition);
        c.moveToPosition(position);

        location.setText(c.getString(c.getColumnIndex("location")));
        mileage.setText(c.getString(c.getColumnIndex("mileage")));
        gallons.setText(c.getString(c.getColumnIndex("quantity")));
        price.setText(c.getString(c.getColumnIndex("price")));
//        dateView.setText(c.getString(c.getColumnIndex("date")));
        dateView.setText(convertTime(c.getInt(c.getColumnIndex("date"))));
    }
    private String convertTime(long l){
        Date dateInSeconds=new Date(l*1000);
        Log.e("date", "date: " + dateInSeconds + " seconds: " + l * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
//        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = sdf.format(dateInSeconds);
        return formatted;
    }

//    public void setFieldsWithData(Cursor c, int position){
//        c.moveToPosition(position);
//        location.setText(c.getString(c.getColumnIndex("location")));
//        mileage.setText(c.getString(c.getColumnIndex("mileage")));
//        gallons.setText(c.getString(c.getColumnIndex("quantity")));
//        price.setText(c.getString(c.getColumnIndex("price")));
//        dateView.setText(c.getString(c.getColumnIndex("date")));
//    }

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
//        convertDateFieldToInt();
    }
    private long convertDateFieldToInt(){
        String dateString= dateView.getText().toString();
        Log.e("somedate","date: "+dateString);
        SimpleDateFormat sdf=new SimpleDateFormat("MMM-dd-yyyy");
        try {
            Date date = sdf.parse(dateString);
            Log.e("date","date: "+date.getTime());
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
    public void onClick(View v) {
        if (v == dateView) {
            Log.e("clicked dateview", "clicked dateview");
            fromDatePickerDialog.show();
        }
        if(v==deleteButton){
            dbHelper.deleteEntry(mId);
            mListener.dismissDialogFragment(getTag());
        }
    }
}