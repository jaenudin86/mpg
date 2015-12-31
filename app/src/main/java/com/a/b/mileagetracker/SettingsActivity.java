package com.a.b.mileagetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.DataAccess.SettingInterfaces;
import com.a.b.mileagetracker.Fragments.AddVehicleFragment;
import com.a.b.mileagetracker.Fragments.VehicleListFragment;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, SettingInterfaces.SettingInterface, AddVehicleFragment.AddVehicle,VehicleListFragment.VehicleList, LoaderManager.LoaderCallbacks<Cursor>{
    String TAG = "SettingsActivity";
    TextView mGarage;
    private AddVehicleFragment mAddVehicleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha, null));
        else
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Switch length=(Switch) findViewById(R.id.switch_distance);
        Switch quantity=(Switch) findViewById(R.id.switch_quantity);
        Button addVehicle = (Button) findViewById(R.id.add_vehicle);
        Button deleteVehicle=(Button) findViewById(R.id.delete_button);

        length.setOnClickListener(this);
        quantity.setOnClickListener(this);
        addVehicle.setOnClickListener(this);
        deleteVehicle.setOnClickListener(this);
        mGarage=(TextView) findViewById(R.id.garage_textview);

//        getAllSharedPrefs();  <= log all shared prefs

        getSupportLoaderManager().initLoader(0, null, this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return false;
    }
//    public void getAllSharedPrefs() {
//        SharedPreferences sharedPrefs = this.getSharedPreferences("prefs", Context.MODE_PRIVATE);
//        Map<String, ?> keys = sharedPrefs.getAll();
//
//        for (Map.Entry<String, ?> entry : keys.entrySet()) {
//            Log.e(TAG, "All values from shared prefs: "+entry.getKey() + ":");
//            try {
//                Log.e(TAG, "value: " + entry.getValue().toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_vehicle) {
            onAddVehicle();
        }
        if (id==R.id.delete_button){
            openVehicleListFragment();
        }
        if(id==R.id.switch_distance||id==R.id.switch_quantity){
            Toast.makeText(this,R.string.future_length_quanity,Toast.LENGTH_SHORT).show();
        }
    }

    public void openVehicleListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        VehicleListFragment vehicleListFragment = VehicleListFragment.newInstance();
        vehicleListFragment.show(fragmentManager, "openVehicleList");
    }
    @Override
    public void openVehicleListDismiss() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    public void onAddVehicle() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mAddVehicleFragment = new AddVehicleFragment().newInstance();
        mAddVehicleFragment.show(fragmentManager, "addVehicle");
    }

    @Override
    public void onAddVehicleDismiss() {
        mAddVehicleFragment.dismiss();
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL=null;
        CL = new CursorLoader(this, Uri.parse("content://com.a.b.mileagetracker/key_table"), null, null, null, null);
        return CL;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String vehicles="";
        data.moveToFirst();
        if(data!=null&&data.getCount()>0) {
            do {
                vehicles += "<b>" + (data.getString(data.getColumnIndex(MySQLiteHelper.KEY_COLUMN_YEAR)) + " " +
                        (data.getString(data.getColumnIndex(MySQLiteHelper.KEY_COLUMN_MAKE))) + " " +
                        (data.getString(data.getColumnIndex(MySQLiteHelper.KEY_COLUMN_MODEL))) + "<br>");

            } while (data.moveToNext());
            mGarage.setText(Html.fromHtml(vehicles));
        }else{
            mGarage.setText("no vehicles created");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}