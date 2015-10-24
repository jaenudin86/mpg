package com.a.b.mileagetracker;

import android.app.Dialog;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.Fragments.DialogFrag;
import com.a.b.mileagetracker.Fragments.GraphFragment;
import com.a.b.mileagetracker.testStuffs.ExportDatabase;
import com.a.b.mileagetracker.testStuffs.MessageEvent;
import com.a.b.mileagetracker.Fragments.AllHistoryFragment;
import com.a.b.mileagetracker.Fragments.OverallStatsFragment;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogFrag.DialogInterface {
    AllHistoryFragment receiverTest;
    OverallStatsFragment overallStatsFragment;
    private MySQLiteHelper dbHelper;
    private SQLiteDatabase db;
    private SimpleCursorAdapter cursorAdapter;
    private ListView listview;
    private DialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        dbHelper = MySQLiteHelper.getInstance(getApplicationContext());
    }

    public void showCarSelectorDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.car_selector_dialog);
        dialog.show();

        LayoutInflater factory=LayoutInflater.from(this);
        View textEntry= factory.inflate(R.layout.car_selector_dialog, null);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

        if (id == R.id.nav_camara) {
            android.support.v4.app.FragmentTransaction ftDialog=getSupportFragmentManager().beginTransaction();
            android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if(prev!=null){
                ftDialog.remove(prev);
            }
            ftDialog.addToBackStack(null);
            newFragment=DialogFrag.newInstance();
//            dialogFrag= DialogFrag.newInstance();
            newFragment.show(ftDialog, "dialog");

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

            dbHelper.getAllData();
        } else if (id == R.id.nav_share) {

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
    public void onDialogAddVehicle() {
        Log.e("touched button","touched button, message received in Activity");
        newFragment.dismiss();

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
