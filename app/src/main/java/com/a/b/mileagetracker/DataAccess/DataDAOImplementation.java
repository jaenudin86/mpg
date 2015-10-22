package com.a.b.mileagetracker.DataAccess;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.UserDictionary;
import android.util.Log;

import com.a.b.mileagetracker.Model.GasEntry;

import java.sql.Date;
import java.sql.Time;
import java.util.logging.Logger;

/**
 * Created by Andrew on 10/12/2015.
 */
public class DataDAOImplementation extends Fragment implements SQLDao{
    private SQLiteDatabase db;
    private MySQLiteHelper dbHelper;

    private void logger(String log){
        Log.e("DataDAOImplementation",log);
    }

    public void getNextRecord(){
    }

    public void createEntry(GasEntry entry){
        ContentValues values = new ContentValues();
//        values.put(MySQLiteHelper.COLUMN_COMMENT, entry);
//        ContentResolver cr;
//        cr.query(UserDictionary.Words.CONTENT_URI, mProjection)
    }

    @Override
    public void createVehicle(String vehicle) {
        logger("createVehicle");
    }

    @Override
    public void addEntry(String vehicle, int miles, double gallons, double price, int date) {
        dbHelper = MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        db=dbHelper.getWritableDatabase();
        logger("addEntry: "+vehicle+", "+miles+", "+gallons+", "+price+", "+date);
    }

    @Override
    public Cursor getAllData() {
        logger("getAllData");
        return null;
    }

    @Override
    public void deleteRecord(int entry) {
        logger("deleteRecord");
    }

    @Override
    public void changeRecord(int id, String vehicle, int miles, double gallons, double price, int date) {
        logger("changeRecord");
    }
}
