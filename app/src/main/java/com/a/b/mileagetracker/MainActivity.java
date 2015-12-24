package com.a.b.mileagetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.DialogInterfaces;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.DataAccess.ToolBarCursorAdapter;
import com.a.b.mileagetracker.Events.RefreshVehiclesEvent;
import com.a.b.mileagetracker.Fragments.AddVehicleFragment;
import com.a.b.mileagetracker.Fragments.AddRecordDialogFrag;
import com.a.b.mileagetracker.Fragments.EditHistoryFragment;
import com.a.b.mileagetracker.Fragments.EmailFragment;
import com.a.b.mileagetracker.Fragments.GraphFragment;
import com.a.b.mileagetracker.Events.RefreshHistoryListViewEvent;
import com.a.b.mileagetracker.Fragments.HistoryFragment;
import com.a.b.mileagetracker.Fragments.StatsFragment;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogInterfaces.DialogInterface, AddVehicleFragment.AddVehicle {
    private SharedPreferences mSharedPrefs;
    private MySQLiteHelper mDBHelper;
    private DialogFragment dialogFragment;
    public static ToolBarCursorAdapter toolBarAdapter;
    FragmentManager fragmentManager = getSupportFragmentManager();
    private boolean backPressedToExitOnce = false;
    private boolean mIsLargeLayout;
    private AddVehicleFragment mAddVehicleFragment;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIsLargeLayout=getResources().getBoolean(R.bool.large_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("clicked??", "clicked maybe yes??");
//                toolbar.setTitle("clicked");
//            }
//        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        openDrawer();
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        if (savedInstanceState == null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            StatsFragment overallStatsFragment = StatsFragment.newInstance();
            ft.replace(R.id.fragment_holder, overallStatsFragment).commit();
//        }

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);

        mDBHelper = MySQLiteHelper.getInstance(getApplicationContext());
        final Cursor c = mDBHelper.getAllDataFromKeyTable();

        toolBarAdapter = new ToolBarCursorAdapter(getApplicationContext(), c, 0);
        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(toolBarAdapter);
        spinner.setOnItemSelectedListener(toolBarAdapter);
        updateSharedPrefsVehicles();
//        }else{
//            Log.e("main activity","getCount<0");
////            toolBarAdapter = new ToolBarCursorAdapter(getApplicationContext(), c, 0);
//        }
    }
    public void openDrawer(){
        mSharedPrefs = getSharedPreferences("prefs", 0);
        String veh=mSharedPrefs.getString("currentVehicle",null);
        if(veh==null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawer.openDrawer(Gravity.LEFT);
                }
            }, 750);
        }
    }
//    public void selectStartupFragment(){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//
//        StatsFragment overallStatsFragment = StatsFragment.newInstance();
//        mSharedPrefs = getSharedPreferences("prefs", 0);
//        if(mSharedPrefs.getString("currentVehicle",null)==null) {
//            StartupFragment startupFragment = new StartupFragment();
//            ft.add(R.id.fragment_holder, startupFragment);
//        }else {
//            ft.add(R.id.fragment_holder, overallStatsFragment);
//            ft.addToBackStack("stats");
//        }
//        ft.commit();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void updateSharedPrefsVehicles() {
        mDBHelper = MySQLiteHelper.getInstance(getApplicationContext());
        Cursor cursor = mDBHelper.getAllDataFromKeyTable();

        mSharedPrefs = getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = mSharedPrefs.edit();

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            ArrayList<String> vehicles = new ArrayList<>();
            do {
                vehicles.add(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.KEY_COLUMN_TABLE)));
            } while (cursor.moveToNext());
            String[] myStringList = vehicles.toArray(new String[vehicles.size()]);
            editor.putString("vehicle_list", TextUtils.join("‚‗‚", myStringList)).apply();
        }else{
            editor.remove("vehicle_list").commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            popBackStack();
        }
    }

//    @Override
//    public void onBackPressed() {
//        if ((backPressedToExitOnce) || (getSupportFragmentManager().getBackStackEntryCount() > 0)) {
//            super.onBackPressed();
//        } else {
//            this.backPressedToExitOnce = true;
//            showToast("Press again to exit ");
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    backPressedToExitOnce = false;
//                }
//            }, 2000);
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

    public void popBackStack() {
        if (backPressedToExitOnce) {
            Log.e("main", "end app???");
            super.onBackPressed();
        }

        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();

        } else {
            Toast.makeText(this, R.string.backpress, Toast.LENGTH_SHORT).show();
            this.backPressedToExitOnce = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 2000);
        }
        Log.e("main", "backpresed boolean: " + backPressedToExitOnce + ", backstackcount: " + fragmentManager.getBackStackEntryCount());
    }

//        if((backPressedToExitOnce) || (fragmentManager.getBackStackEntryCount() != 0)) {
//            Log.e("main", "backstack count: " + fragmentManager.getBackStackEntryCount());
//
////            super.onBackPressed();
//        }
//            Log.e("main","popbackstack else clause");
//            this.backPressedToExitOnce = true;
////            Toast.makeText(getApplicationContext(), "Press again to exit ", Toast.LENGTH_LONG);
//            showToast("message");
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    backPressedToExitOnce = false;
//                }
//            }, 2000);
//
//    }
//    private void showToast(String message) {
//        if (this.toast == null) {
//            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
//        } else if (this.toast.getView() == null) {
//            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
//        } else {
//            this.toast.setText(message);
//        }
//        this.toast.show();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.e("settings", "settings selected00 actionbar");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.e("settings", "settings selected00");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        int id = item.getItemId();

        if (id == R.id.nav_add_record) {
            pressInitialButtonAction();

        } else if (id == R.id.nav_history_list) {
            HistoryFragment allDataListViewFragment = new HistoryFragment();
            ft.replace(R.id.fragment_holder, allDataListViewFragment);
            ft.commit();

            EventBus.getDefault().post(new RefreshHistoryListViewEvent("hello test from Activity"));

        } else if (id == R.id.nav_stats) {
//            if(!overallStatsFragment.isAdded()) {
            StatsFragment overallStatsFragment = StatsFragment.newInstance();
            ft.replace(R.id.fragment_holder, overallStatsFragment).commit();

        } else if (id == R.id.nav_graph) {
            GraphFragment graphFrag = GraphFragment.newInstance("sending message");
            ft.replace(R.id.fragment_holder, graphFrag).commit();

        } else if (id == R.id.nav_settings) {
//            SettingsFragment settingsFragment = SettingsFragment.newInstance();
//            ft.replace(R.id.fragment_holder, settingsFragment);

//            if(mIsLargeLayout){
//                settingsFragment.show(fragmentManager,"dialog");
//            }else {
//                Log.e("main","small screen layout");
                Intent intent=new Intent(this,SettingsActivity.class);
//                intent.putExtra("test",60);
                startActivity(intent);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
////            findViewById(android.R.id.content);
//                popBackStack();
//                ft.add(android.R.id.content, settingsFragment);
////                ft.addToBackStack(null)
//                ft.commit();
//            }

//            ft.addToBackStack(null);
//            ft.commit();
        } else if (id == R.id.nav_send) {
//            ExportDatabaseAsyncTask exportDb=new ExportDatabaseAsyncTask(getApplicationContext());
//            exportDb.execute();

//            Intent emailIntent=new Intent(MainActivity.this,SendEmailActivity.class);
//            emailIntent.putExtra("attachent","testStringAttachment");
//            MainActivity.this.startActivity(emailIntent);

            new AlertDialog.Builder(this)
                .setTitle("Export Data")
                .setMessage("This will create a spreadsheet of all your collected data. You can send it by email to yourself in the next screens!")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        Log.e("Main", "clicked yep");
                        EmailFragment emailFragment = new EmailFragment();
                        ft.add(emailFragment, "emailFragment");
                        ft.commit();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("main", "nope");
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void pressInitialButtonAction() {
        if (mDBHelper.keyTableHasData() == false) {
            onAddVehicle();
        } else {
            android.support.v4.app.FragmentTransaction ftDialog = getSupportFragmentManager().beginTransaction();
            android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ftDialog.remove(prev);
            }
            ftDialog.addToBackStack(null);
            dialogFragment = AddRecordDialogFrag.newInstance();
            dialogFragment.show(ftDialog, "dialog");
        }
    }

    @Override
    public void dismissDialogFragment(String tag) {
        DialogFragment dF = (DialogFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (dF != null) {
            dF.dismiss();
            mDBHelper.calculateMpgColumn();
        }
    }
    public void onEvent(RefreshVehiclesEvent event){
        updateToolBarView();
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
//    @Override
//    public void openVehicleListFragment() {
////        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        VehicleListFragment vehicleListFragment = VehicleListFragment.newInstance();
//        Log.e("main", "stack: " + fragmentManager.getBackStackEntryCount());
//        ft.addToBackStack(null);
//        ft.replace(R.id.fragment_holder, vehicleListFragment);
//        ft.setTransition(ft.TRANSIT_FRAGMENT_OPEN);
//        ft.commit();
//    }

    @Override
    public void updateToolBarView() {
        mDBHelper = MySQLiteHelper.getInstance(getApplicationContext());
        Cursor cursor = mDBHelper.getAllDataFromKeyTable();
        toolBarAdapter.changeCursor(cursor);
        toolBarAdapter.notifyDataSetChanged();
        updateSharedPrefsVehicles();
    }

    @Override
    public void openEditVehicleEntryFragment() {
        EditHistoryFragment editHistoryLineItem = new EditHistoryFragment();
        editHistoryLineItem.show(getSupportFragmentManager(), "editLineItem");
//        editHistoryLineItem.setFieldsWithData();
    }
//
//    @Override
//    public void onEditDate() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        DatePicker datePicker = new DatePicker();
//        ft.replace(R.id.fragment_holder, datePicker).commit();
//        Log.e("onEditDate", "onEditDate");
//    }
//
//    @Override
//    public void selectCurrentCar(String make, String model, int year) {
//        String currentCar = make + model + year;
//        SharedPreferences settings = getSharedPreferences("prefs", 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("currentVehicle", currentCar);
//        editor.commit();
//
//        Log.e("shared preferences", "shared prefs: " + currentCar);
//    }

    @Override
    public void onAddVehicle() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mAddVehicleFragment = new AddVehicleFragment().newInstance();
        mAddVehicleFragment.show(fragmentManager, "addVehicle");
    }

    @Override
    public void onAddVehicleDismiss() {
        mAddVehicleFragment.dismiss();
        updateToolBarView();
    }
}