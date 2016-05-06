package com.radicaldroids.mileage;

import android.database.Cursor;
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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.radicaldroids.mileage.DataAccess.DataProvider;
import com.radicaldroids.mileage.DataAccess.SQLiteHelper;
import com.radicaldroids.mileage.DataAccess.SettingInterfaces;
import com.radicaldroids.mileage.Fragments.AddVehicleFragment;
import com.radicaldroids.mileage.Fragments.VehicleListFragment;

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

        Button addVehicle = (Button) findViewById(R.id.add_vehicle);
        Button deleteVehicle=(Button) findViewById(R.id.delete_button);

        addVehicle.setOnClickListener(this);
        deleteVehicle.setOnClickListener(this);
        mGarage=(TextView) findViewById(R.id.garage_textview);

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
        CL = new CursorLoader(this, Uri.parse(DataProvider.BASE_CONTENT_URI +"/key_table"), null, null, null, null);
        return CL;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String vehicles="";
        if(data!=null&&data.getCount()>0) {
            data.moveToFirst();
            do {
                vehicles += "<b>" + (data.getString(data.getColumnIndex(SQLiteHelper.KEY_COLUMN_YEAR)) + " " +
                        (data.getString(data.getColumnIndex(SQLiteHelper.KEY_COLUMN_MAKE))) + " " +
                        (data.getString(data.getColumnIndex(SQLiteHelper.KEY_COLUMN_MODEL))) + "<br>");

            } while (data.moveToNext());
            mGarage.setText(Html.fromHtml(vehicles));
        }else{
            mGarage.setText(R.string.no_vehicles_created);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}