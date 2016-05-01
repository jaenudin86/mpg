package com.radicaldroids.mileage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

import com.radicaldroids.mileage.DataAccess.DataProvider;
import com.radicaldroids.mileage.DataAccess.DialogInterfaces;
import com.radicaldroids.mileage.DataAccess.SQLiteHelper;
import com.radicaldroids.mileage.DataAccess.ToolBarCursorAdapter;
import com.radicaldroids.mileage.Events.RefreshVehiclesEvent;
import com.radicaldroids.mileage.Fragments.AddVehicleFragment;
import com.radicaldroids.mileage.Fragments.AddRecordDialogFrag;
import com.radicaldroids.mileage.Fragments.EditHistoryFragment;
import com.radicaldroids.mileage.Fragments.GraphFragment;
import com.radicaldroids.mileage.Events.RefreshHistoryListViewEvent;
import com.radicaldroids.mileage.Fragments.HistoryFragment;
import com.radicaldroids.mileage.Fragments.StatsFragment;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DialogInterfaces.DialogInterface, AddVehicleFragment.AddVehicle {
    private SharedPreferences mSharedPrefs;
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

        ((MyApplication)getApplication()).startTracking();

        mIsLargeLayout=getResources().getBoolean(R.bool.large_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAdd();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        openDrawer();
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        if (savedInstanceState == null) {
            //StatsFragment is the app starting page
            FragmentTransaction ft = fragmentManager.beginTransaction();
            StatsFragment overallStatsFragment = StatsFragment.newInstance();
            ft.replace(R.id.fragment_holder, overallStatsFragment).commit();
//        }

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);

        final Cursor cursor=getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI +"/key_table"),null,null,null,null);

        toolBarAdapter = new ToolBarCursorAdapter(getApplicationContext(), cursor, 0);
        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(toolBarAdapter);
        spinner.setOnItemSelectedListener(toolBarAdapter);
//        updateSharedPrefsVehicles();
//        }else{
//            Log.e("main activity","getCount<0");
////            toolBarAdapter = new ToolBarCursorAdapter(getApplicationContext(), c, 0);
//        }
    }
    public void openDrawer(){
        //if no vehicles are created, open the drawer to show the user where to control the app
        mSharedPrefs = getSharedPreferences(Constants.SHARED_PREFS, 0);
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
//    // Load a TagManager container
//    public void loadGTMContainer () {
//        // TODO Get the TagManager
//        mTagManager = ((MyApplication) getApplication()).getTagManager();
//
//        // Enable verbose logging
//        mTagManager.setVerboseLoggingEnabled(true);
//
//        // Load the container
//        PendingResult pending = mTagManager.loadContainerPreferFresh("GTM-123456",R.raw.gtm_default);
//
//        // Define the callback to store the loaded container
//        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
//            @Override
//            public void onResult(ContainerHolder containerHolder) {
//
//                // If unsuccessful, return
//                if (!containerHolder.getStatus().isSuccess()) {
//                    // Deal with failure
//                    return;
//                }
//
//                // Manually refresh the container holder
//                // Can only do this once every 15 minutes or so
//                containerHolder.refresh();
//
//                // Set the container holder, only want one per running app
//                // We can retrieve it later as needed
//                ((MyApplication) getApplication()).setContainerHolder(
//                        containerHolder);
//
//            }
//        }, 2, TimeUnit.SECONDS);
//    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

//    @Override
//    public void updateSharedPrefsVehicles() {
//        final Cursor cursor=getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI +"/key_table"),null,null,null,null);
//
//        mSharedPrefs = getSharedPreferences(Constants.SHARED_PREFS, 0);
//        SharedPreferences.Editor editor = mSharedPrefs.edit();
//
//        cursor.moveToFirst();
//        if (cursor.getCount() > 0) {
//            ArrayList<String> vehicles = new ArrayList<>();
//            do {
//                vehicles.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COLUMN_TABLE)));
//            } while (cursor.moveToNext());
//            String[] myStringList = vehicles.toArray(new String[vehicles.size()]);
//            editor.putString("vehicle_list", TextUtils.join("‚‗‚", myStringList)).apply();
//        }else{
//            editor.remove("vehicle_list").commit();
//        }
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            popBackStack();
        }
    }

    public void popBackStack() {
        if (backPressedToExitOnce) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
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
            fabAdd();

        } else if (id == R.id.nav_history_list) {
            HistoryFragment allDataListViewFragment = new HistoryFragment();
            ft.replace(R.id.fragment_holder, allDataListViewFragment);
            ft.commit();

            EventBus.getDefault().post(new RefreshHistoryListViewEvent("hello test from Activity"));

        } else if (id == R.id.nav_stats) {
            StatsFragment overallStatsFragment = StatsFragment.newInstance();
            ft.replace(R.id.fragment_holder, overallStatsFragment).commit();

        } else if (id == R.id.nav_graph) {
            GraphFragment graphFrag = GraphFragment.newInstance("sending message");
            ft.replace(R.id.fragment_holder, graphFrag).commit();

        } else if (id == R.id.nav_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            final Context context=this;
            new AlertDialog.Builder(this)
                .setTitle(R.string.export_data_title)
                .setMessage(R.string.alert_email_to_yourself)
                .setPositiveButton(R.string.alert_send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SpreadsheetCreator(context).execute();
                    }
                })
                .setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.taxi)
                .show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void fabAdd() {
        SharedPreferences sharedPrefs=getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        String currentVehicle= sharedPrefs.getString(Constants.SHARED_PREFS_CURRENT_VEHICLE, null);

        if(currentVehicle==null){
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
            SQLiteHelper mDBHelper = SQLiteHelper.getInstance(getApplicationContext());
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
        //Update widget when leaving app
        Intent dataUpdatedIntent = new Intent(Constants.WIDGET_UPDATE).setPackage(getPackageName());
        sendBroadcast(dataUpdatedIntent);
        super.onStop();
    }

    @Override
    public void updateToolBarView() {
        final Cursor cursor=getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI +"/key_table"),null,null,null,null);
        toolBarAdapter.changeCursor(cursor);
        toolBarAdapter.notifyDataSetChanged();
//        updateSharedPrefsVehicles();
    }

    @Override
    public void openEditVehicleEntryFragment() {
        EditHistoryFragment editHistoryLineItem = new EditHistoryFragment();
        editHistoryLineItem.show(getSupportFragmentManager(), "editLineItem");
    }

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