package com.a.b.mileagetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.a.b.mileagetracker.DataAccess.SettingInterfaces;
import com.a.b.mileagetracker.Fragments.AddVehicleFragment;
import com.a.b.mileagetracker.Fragments.VehicleListFragment;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, SettingInterfaces.SettingInterface{
    String TAG = "SettingsActivity";
    private AddVehicleFragment mAddVehicleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPrefs = this.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        Log.e(TAG, "value from SharedPrefs.. currentVehicleGUI: " + sharedPrefs.getString("currentVehicleGUI", "null"));
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha, null));
        else
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button addEditVehicle = (Button) findViewById(R.id.add_edit_vehicle);
        addEditVehicle.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_edit_vehicle) {
            Log.e("button pushed", "button pushed");
            onDialogAddVehicle();
        }
        if (id==R.id.delete_button){
            openVehicleListFragment();
        }
    }

    @Override
    public void onDialogAddVehicle() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        mAddVehicleFragment = new AddVehicleFragment().newInstance();
        mAddVehicleFragment.show(fragmentManager, "addVehicle");
    }

    @Override
    public void onDialogAddVehicleDismiss() {
        mAddVehicleFragment.dismiss();
    }

    public void openVehicleListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        VehicleListFragment vehicleListFragment = VehicleListFragment.newInstance();
        Log.e("main", "stack: " + fragmentManager.getBackStackEntryCount());
//        ft.addToBackStack(null);
        ft.add(android.R.id.content, vehicleListFragment);
        ft.setTransition(ft.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}