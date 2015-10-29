package com.a.b.mileagetracker;

import android.app.Dialog;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.DialogInterfaces;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.DataAccess.ToolBarCursorAdapter;
import com.a.b.mileagetracker.Fragments.AddVehicleDialogFrag;
import com.a.b.mileagetracker.Fragments.DatePicker;
import com.a.b.mileagetracker.Fragments.AddRecordDialogFrag;
import com.a.b.mileagetracker.Fragments.EditHistoryEntryFragment;
import com.a.b.mileagetracker.Fragments.GraphFragment;
import com.a.b.mileagetracker.testStuffs.ExportDatabase;
import com.a.b.mileagetracker.Model.MessageEvent;
import com.a.b.mileagetracker.Fragments.AllHistoryFragment;
import com.a.b.mileagetracker.Fragments.OverallStatsFragment;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogInterfaces.DialogInterface {
    OverallStatsFragment overallStatsFragment;
    private MySQLiteHelper mDBHelper;
    private SimpleCursorAdapter cursorAdapter;
    private ListView listview;
    private DialogFragment dialogFragment;
    private AddVehicleDialogFrag addVehicleDialogFrag;
    private EditHistoryEntryFragment mEditEntryData;
    private SharedPreferences mSharedPrefs;
    public static ToolBarCursorAdapter toolBarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked??", "clicked maybe yes??");
                toolbar.setTitle("clicked");

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        receiverTest = new AllHistoryFragment();
        overallStatsFragment = new OverallStatsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fragment_holder, overallStatsFragment);
        ft.addToBackStack("stats");
        ft.commit();

//        'getApplicationContext' to help with garbage collection
//        dbHelper = new MySQLiteHelper(getApplicationContext());
//        dbHelper = MySQLiteHelper.getInstance(getApplicationContext());

//        int abTitleId=getResources().getIdentifier("action_bar_title", "id","android");
//        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("clicked the bar","clicked!!!!!!!");
//            }
//        });

        mSharedPrefs=getSharedPreferences("prefs",0);
        getSupportActionBar().setTitle(mSharedPrefs.getString("currentVehicleGUI", "defValue"));

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, toolbar,false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);

//        ToolbarSpinnerAdapter spinnerAdapter = new ToolbarSpinnerAdapter(getApplicationContext());//
//        TestObject testObj1=new TestObject();
//        TestObject testObj2=new TestObject();
//        testObj1.setName("97 Honda Accord");
//        testObj2.setName("2008 Ferrari Murciélago");
//        List to = new ArrayList<TestObject>();
//        to.add(testObj1);
//        to.add(testObj2);//
//        spinnerAdapter.addItems(to);
//        Spinner spinner =(Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
//        spinner.setAdapter(spinnerAdapter);

        mDBHelper = MySQLiteHelper.getInstance(getApplicationContext());
        final Cursor c=mDBHelper.getAllDataFromKeyTable();
        c.moveToFirst();
        Log.e("cursor", "cursor: " + c);

        toolBarAdapter = new ToolBarCursorAdapter(getApplicationContext(), c,0);
        Spinner spinner =(Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(toolBarAdapter);
        spinner.setOnItemSelectedListener(toolBarAdapter);
    }

    public void showCarSelectorDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_record);
        dialog.show();

        LayoutInflater factory=LayoutInflater.from(this);
        View textEntry= factory.inflate(R.layout.add_record, null);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.e("settings","settings selected00 actionbar");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.e("settings","settings selected00");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_record) {
            if(mDBHelper.keyTableHasData()==false){
                AddVehicleDialogFrag addVehicle=new AddVehicleDialogFrag().newInstance();
                addVehicle.show(fragmentManager,"addVehicle");
            }else {

                android.support.v4.app.FragmentTransaction ftDialog = getSupportFragmentManager().beginTransaction();
                android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ftDialog.remove(prev);
                }
                ftDialog.addToBackStack(null);
                dialogFragment = AddRecordDialogFrag.newInstance();
                dialogFragment.show(ftDialog, "dialog");
            }

            // Handle the camera action
        } else if (id == R.id.nav_history_list) {

            AllHistoryFragment allDataListViewFragment = new AllHistoryFragment();
            ft.replace(R.id.fragment_holder, allDataListViewFragment);
            ft.commit();

            EventBus.getDefault().post(new MessageEvent("hello test from Activity"));

        } else if (id == R.id.nav_stats) {

//            if(!overallStatsFragment.isAdded()) {
                ft.replace(R.id.fragment_holder, overallStatsFragment);
                ft.commit();

        } else if (id == R.id.nav_graph) {
            GraphFragment graphFrag= GraphFragment.newInstance("sending message");
            ft.replace(R.id.fragment_holder, graphFrag).commit();
            mDBHelper.getAllData();
        } else if (id == R.id.nav_settings) {

            addVehicleDialogFrag=new AddVehicleDialogFrag().newInstance();
            addVehicleDialogFrag.show(fragmentManager,"addVehicle");


        } else if (id == R.id.nav_send) {

            ExportDatabase exportDb=new ExportDatabase(getApplicationContext());
            exportDb.execute();

            Toast.makeText(this,"pressed export button",Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDialogAddEntryDismiss() {
        dialogFragment.dismiss();
    }

    @Override
    public void onDialogAddVehicleDismiss() {
        addVehicleDialogFrag.dismiss();

        mDBHelper = MySQLiteHelper.getInstance(getApplicationContext());
        Cursor cursor=mDBHelper.getAllDataFromKeyTable();
        toolBarAdapter.changeCursor(cursor);
        toolBarAdapter.notifyDataSetChanged();
    }

    @Override
    public void openEditVehicleEntryFragment() {
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        Fragment mEditEntryData = new EditHistoryEntryFragment().newInstance();
//        ft.replace(R.id.fragment_holder, mEditEntryData);
//        mEditEntryData.show(ft, "editEntry");

        EditHistoryEntryFragment editHistoryLineItem = new EditHistoryEntryFragment();
        editHistoryLineItem.show(getSupportFragmentManager(),"editLineItem");

    }

    @Override
    public void onEditDate() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        DatePicker datePicker = new DatePicker();
        ft.replace(R.id.fragment_holder, datePicker).commit();
        Log.e("onEditDate","onEditDate");
    }

    @Override
    public void selectCurrentCar(String make, String model, int year) {
        String currentCar=make+model+year;
        SharedPreferences settings = getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("currentVehicle",currentCar);
        editor.commit();

        Log.e("shared preferences","shared prefs: "+currentCar);
    }
//    public void testExport(){
//        boolean success = false;
//
//        String currentDateString = new SimpleDateFormat(SyncStateContract.Constants.SimpleDtFrmt_ddMMyyyy).format(new Date());
//
//        File dbFile = getDatabasePath("HLPL_FRETE.db");
//        Log.e(TAG, "Db path is: " + dbFile); // get the path of db
//        File exportDir = new File(Environment.getExternalStorageDirectory() + File.separator + SyncStateContract.Constants.FileNm.FILE_DIR_NM, "");
//
//        long freeBytesInternal = new File(getFilesDir().getAbsoluteFile().toString()).getFreeSpace();
//        long megAvailable = freeBytesInternal / 1048576;
//
//        if (megAvailable < 0.1) {
//            System.out.println("Please check"+megAvailable);
//            memoryErr = true;
//        }else {
//            exportDirStr = exportDir.toString();// to show in dialogbox
//            Log.v(TAG, "exportDir path::" + exportDir);
//            if (!exportDir.exists()) {
//                exportDir.mkdirs();
//            }
//            try {
//                List<SalesActivity> listdata = salesLst;
//                SalesActivity sa = null;
//                String lob = null;
//                for (int index = 0; index < listdata.size();) {
//                    sa = listdata.get(index);
//                    lob = sa.getLob();
//                    break;
//                }
//                if (SyncStateContract.Constants.Common.OCEAN_LOB.equals(lob)) {
//
//                    file = new File(exportDir, SyncStateContract.Constants.FileNm.FILE_OFS + currentDateString + ".csv");
//                } else {
//                    file = new File(exportDir, SyncStateContract.Constants.FileNm.FILE_AFS + currentDateString + ".csv");
//                }
//                file.createNewFile();
//                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
//
//
//                // this is the Column of the table and same for Header of CSV
//                // file
//                if (SyncStateContract.Constants.Common.OCEAN_LOB.equals(lob)) {
//                    csvWrite.writeNext(SyncStateContract.Constants.FileNm.CSV_O_HEADER);
//                }else{
//                    csvWrite.writeNext(SyncStateContract.Constants.FileNm.CSV_A_HEADER);
//                }
//                String arrStr1[] = { "SR.No", "CUTSOMER NAME", "PROSPECT", "PORT OF LOAD", "PORT OF DISCHARGE" };
//                csvWrite.writeNext(arrStr1);
//
//                if (listdata.size() > 0) {
//                    for (int index = 0; index < listdata.size(); index++) {
//                        sa = listdata.get(index);
//                        String pol;
//                        String pod;
//                        if (SyncStateContract.Constants.Common.OCEAN_LOB.equals(sa.getLob())) {
//                            pol = sa.getPortOfLoadingOENm();
//                            pod = sa.getPortOfDischargeOENm();
//                        } else {
//                            pol = sa.getAirportOfLoadNm();
//                            pod = sa.getAirportOfDischargeNm();
//                        }
//                        int srNo = index;
//                        String arrStr[] = { String.valueOf(srNo + 1), sa.getCustomerNm(), sa.getProspectNm(), pol, pod };
//                        csvWrite.writeNext(arrStr);
//                    }
//                    success = true;
//                }
//                csvWrite.close();
//
//            } catch (IOException e) {
//                Log.e("SearchResultActivity", e.getMessage(), e);
//                return success;
//            }
//        }
//        return success;
//    }
}
