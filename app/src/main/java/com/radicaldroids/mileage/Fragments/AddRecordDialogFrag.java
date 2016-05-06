package com.radicaldroids.mileage.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.radicaldroids.mileage.Constants;
import com.radicaldroids.mileage.DataAccess.DataProvider;
import com.radicaldroids.mileage.DataAccess.DialogInterfaces;
import com.radicaldroids.mileage.DataAccess.SQLiteHelper;
import com.radicaldroids.mileage.Events.RefreshHistoryListViewEvent;
import com.radicaldroids.mileage.MyApplication;
import com.radicaldroids.mileage.R;


import org.apache.commons.lang3.text.WordUtils;

import java.text.DecimalFormat;
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

    DialogInterfaces.DialogInterface mListener;
    private SQLiteHelper dbHelper;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText dateView;
    private Tracker mTracker;

    public AddRecordDialogFrag(){}

    public static AddRecordDialogFrag newInstance(){
        return new AddRecordDialogFrag();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbHelper= SQLiteHelper.getInstance(getActivity().getApplicationContext());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_record, null);

        MyApplication application=(MyApplication) getActivity().getApplication();
        mTracker=application.getTracker();
        sendAnalyticName();

        final EditText location= (EditText) view.findViewById(R.id.station_location);
        final EditText mileage = (EditText) view.findViewById(R.id.mileage);
        final EditText gallons = (EditText) view.findViewById(R.id.gallons);
        final EditText price = (EditText) view.findViewById(R.id.price);

        final SharedPreferences sharedPrefs=getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        final TextView vehicle = (TextView) view.findViewById(R.id.current_vehicle_add_record_frag);
        vehicle.setText(sharedPrefs.getString(Constants.SHARED_PREFS_CURRENT_VEHICLE_GUI, "car"));

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
                DecimalFormat df3=new DecimalFormat("#.###");
                DecimalFormat df2=new DecimalFormat("0.00");

                ContentValues values=new ContentValues();

                values.put(SQLiteHelper.COLUMN_ODOMETER,(int) Math.round(Double.parseDouble(mileage.getText().toString())));
                values.put(SQLiteHelper.COLUMN_QUANTITY,Double.valueOf(df3.format(Double.parseDouble(gallons.getText().toString()))));
                values.put(SQLiteHelper.COLUMN_PRICE,Double.valueOf(df2.format(Double.parseDouble(price.getText().toString()))));
                values.put(SQLiteHelper.COLUMN_DATE,convertDateFieldToInt());
                values.put(SQLiteHelper.COLUMN_LOCATION,WordUtils.capitalizeFully(location.getText().toString()));

                getActivity().getContentResolver().insert(Uri.parse(DataProvider.BASE_CONTENT_URI +"/fillup"),values);

                mListener.dismissDialogFragment(getTag());
                EventBus.getDefault().postSticky(new RefreshHistoryListViewEvent("refreshing"));
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(),R.string.wrong_number_format,Toast.LENGTH_LONG).show();
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogInterfaces.DialogInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticName();
    }
    private void sendAnalyticName(){
        mTracker.setScreenName(getString(R.string.add_record_analytic_tag));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onClick(View v) {
        if (v == dateView) {
            fromDatePickerDialog.show();
        }
    }
}
