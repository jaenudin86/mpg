package com.a.b.mileagetracker;

import android.app.Dialog;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
import com.a.b.mileagetracker.testStuffs.DialogFrag;
import com.a.b.mileagetracker.testStuffs.MessageEvent;
import com.a.b.mileagetracker.testStuffs.ReceiverTestFragment;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogFrag.DialogInterface {
    ReceiverTestFragment receiverTest;
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

        receiverTest = new ReceiverTestFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fragment_holder, receiverTest);
        ft.commit();

//        'getApplicationContext' to help with garbage collection
//        dbHelper = new MySQLiteHelper(getApplicationContext());
        dbHelper = MySQLiteHelper.getInstance(getApplicationContext());



    }
//    public void getData(){
////        'getReadableDatabase' sets up the database, notice the SQLiteDatabase parent class initialization
//        db =dbHelper.getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT * FROM fillupTable", null);
//        String names="";
//        for(String s: c.getColumnNames()){
//            names=names+s+", ";
//        }
//        Log.e("names","column names: "+names);
//
//        if(c.moveToFirst()){
//            String record="";
//            do{
//                record= String.format("%d ,%.1f ,%d miles, location %s",
//                        c.getInt(0),
//                        c.getFloat(1),
//                        c.getInt(2),
//                        c.getString(3));
//
//                Log.e("record", "record: "+record);
//
//            }while(c.moveToNext());
//        }
//
//    }
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            android.support.v4.app.FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if(prev!=null){
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            newFragment=DialogFrag.newInstance();
//            dialogFrag= DialogFrag.newInstance();
            newFragment.show(ft, "dialog");



            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            EventBus.getDefault().post(new MessageEvent("hello test from Activity"));

        } else if (id == R.id.nav_slideshow) {

//            dialogFrag=new DialogFrag();
//            dialogFrag.setTargetFragment(this, YES_NO_CALL);
//            dialogFrag.show(getFragmentManager(),"tag");

            dbHelper.getAllData();
//            getData();


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
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
}
