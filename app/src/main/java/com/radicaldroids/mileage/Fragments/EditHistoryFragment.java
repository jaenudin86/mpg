package com.radicaldroids.mileage.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
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
import com.radicaldroids.mileage.Events.EditHistoryEvent;
import com.radicaldroids.mileage.Events.RefreshHistoryListViewEvent;
import com.radicaldroids.mileage.MyApplication;
import com.radicaldroids.mileage.R;


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
public class EditHistoryFragment extends DialogFragment implements View.OnClickListener{

    DialogInterfaces.DialogInterface mListener;
    private SQLiteHelper mDbHelper;
    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat mDateFormatter;
    private EditText mDateView;
    private EditText mLocation;
    private EditText mOdometer;
    private EditText mGallons;
    private EditText mPrice;
    private Button mDeleteButton;
    private Button mEditButton;
    private long mId;
    private Tracker mTracker;

    String TAG="EditHistoryFragment";

    public EditHistoryFragment(){
    }

//    public static EditHistoryFragment newInstance(){
//        return new EditHistoryFragment();
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyApplication application=(MyApplication) getActivity().getApplication();
        mTracker=application.getTracker();
        sendAnalyticName();

        mDbHelper = SQLiteHelper.getInstance(getActivity().getApplicationContext());

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
//        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);

        LayoutInflater inflater=getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.edit_record_old_view, null);

        mLocation = (EditText) view.findViewById(R.id.edit_station_location);
        mOdometer = (EditText) view.findViewById(R.id.edit_mileage);
        mGallons = (EditText) view.findViewById(R.id.edit_gallons);
        mPrice = (EditText) view.findViewById(R.id.edit_price);
        mDateView = (EditText) view.findViewById(R.id.edit_date);

        SharedPreferences sharedPrefs=getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        final TextView vehicle = (TextView) view.findViewById(R.id.current_vehicle_edit_record_frag);
        vehicle.setText(sharedPrefs.getString("currentVehicleGUI", "car"));

        mDateView.setInputType(InputType.TYPE_NULL);
        mDateView.setOnClickListener(this);

        mDateFormatter =new SimpleDateFormat("MMM-dd-yyyy", Locale.US);

        setDateField();

        mEditButton =(Button) view.findViewById(R.id.edit_details_submit_button);
        mEditButton.setOnClickListener(this);

        mDeleteButton =(Button) view.findViewById(R.id.delete_details_button);
        mDeleteButton.setOnClickListener(this);
//        builder.setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                try{
//                    NumberFormat formatNumber=NumberFormat.getCurrencyInstance();
//                    Number pNumber=formatNumber.parse(mPrice.getText().toString());
//                    DecimalFormat df3=new DecimalFormat("#.###");
//
//                    mDbHelper.addEntry(
//                            (int) Math.round(Double.parseDouble(mOdometer.getText().toString())),
//                            Double.parseDouble(df3.format(Double.parseDouble(mGallons.getText().toString()))),
//                            Double.parseDouble(pNumber.toString()),
//                            convertDateFieldToInt(),
//                            mLocation.getText().toString());
//                    mListener.dismissDialogFragment(getTag());
//                    mDbHelper.deleteEntry(mId);
//                    EventBus.getDefault().post(new RefreshHistoryListViewEvent("refresh history listview"));
//                } catch (NumberFormatException e) {
//                    Toast.makeText(getActivity(),"wrong number format",Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }catch (ParseException e){
//
//                }
//            }
//        }).setNegativeButton("Delete Record", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mDbHelper.deleteEntry(mId);
//                mListener.dismissDialogFragment(getTag());
//                EventBus.getDefault().post(new RefreshHistoryListViewEvent("refresh historyListView"));
//            }
//        });
        builder.setView(view);
//        builder.setPositiveButton("Edit",null).setNegativeButton("Delete",null).setNeutralButton("cancel",null);
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
//        Log.e("event clicked", " clickedclickedclicked position==> " + editHistoryEvent.mPosition);
        c.moveToPosition(position);

        mLocation.setText(c.getString(c.getColumnIndex("location")));
        mOdometer.setText(c.getString(c.getColumnIndex("odometer")));
        mGallons.setText(c.getString(c.getColumnIndex("quantity")));

        Double pD=c.getDouble(c.getColumnIndex("price"));
        mPrice.addTextChangedListener(textWatcher);
        mPrice.setText(NumberFormat.getCurrencyInstance().format(pD));

        mDateView.setText(convertTime(c.getInt(c.getColumnIndex("date"))));
    }
    TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().matches("^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$")) {
                String userInput = "" + s.toString().replaceAll("[^\\d]", "");
                StringBuilder cashAmountBuilder = new StringBuilder(userInput);

                while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                    cashAmountBuilder.deleteCharAt(0);
                }
                while (cashAmountBuilder.length() < 3) {
                    cashAmountBuilder.insert(0, '0');
                }
                cashAmountBuilder.insert(cashAmountBuilder.length() - 2, '.');

                mPrice.removeTextChangedListener(this);
                mPrice.setText(cashAmountBuilder.toString());

                mPrice.setTextKeepState("$" + cashAmountBuilder.toString());
                Selection.setSelection(mPrice.getText(), cashAmountBuilder.toString().length() + 1);

                mPrice.addTextChangedListener(this);
            }
        }
    };

    private String convertTime(long l){
        Date dateInSeconds=new Date(l*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
//        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = sdf.format(dateInSeconds);
        return formatted;
    }

    private void setDateField(){
        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDateView.setText(mDateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private long convertDateFieldToInt(){
        String dateString= mDateView.getText().toString();
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
    public void onClick(View v) {
        if (v == mDateView) {
            mDatePickerDialog.show();
        }
        if(v== mDeleteButton){
            new AlertDialog.Builder(v.getContext())
                .setTitle(R.string.delete_record)
                .setMessage(R.string.alert_dialog_delete_record_message)
                .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        mDbHelper.deleteEntry(mId);
                        getActivity().getContentResolver().delete(Uri.parse(DataProvider.BASE_CONTENT_URI +"/delete_entry"),Long.toString(mId),null);
                        mListener.dismissDialogFragment(getTag());
                        EventBus.getDefault().post(new RefreshHistoryListViewEvent("refresh historyListView"));
                    }
                })
                .setNegativeButton(R.string.alert_dialog_no, null)
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                .setIcon(R.drawable.alert_48x48)
                .show();
        }
        if(v== mEditButton){
            try{
                NumberFormat formatNumber=NumberFormat.getCurrencyInstance();
                Number pNumber=formatNumber.parse(mPrice.getText().toString());
                DecimalFormat df3=new DecimalFormat("#.###");
                DecimalFormat df2=new DecimalFormat("0.00");

                ContentValues values=new ContentValues();

                values.put(SQLiteHelper.COLUMN_ODOMETER,(int) Math.round(Double.parseDouble(mOdometer.getText().toString())));
                values.put(SQLiteHelper.COLUMN_QUANTITY,Double.valueOf(df3.format(Double.parseDouble(mGallons.getText().toString()))));
                values.put(SQLiteHelper.COLUMN_PRICE,Double.valueOf(df2.format(Double.parseDouble(pNumber.toString()))));
                values.put(SQLiteHelper.COLUMN_DATE,convertDateFieldToInt());
                values.put(SQLiteHelper.COLUMN_LOCATION, WordUtils.capitalizeFully(mLocation.getText().toString()));

                getActivity().getContentResolver().insert(Uri.parse(DataProvider.BASE_CONTENT_URI +"/fillup"),values);

                getActivity().getContentResolver().delete(Uri.parse(DataProvider.BASE_CONTENT_URI +"/delete_entry"),Long.toString(mId),null);
                mListener.dismissDialogFragment(getTag());
                EventBus.getDefault().post(new RefreshHistoryListViewEvent("refresh history listview"));
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(),R.string.wrong_number_format,Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }catch (ParseException e){
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticName();
    }

    private void sendAnalyticName(){
        mTracker.setScreenName(getString(R.string.edit_history_analytic_tag));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}