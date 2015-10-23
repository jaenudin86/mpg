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
public class DataDAOImplementation extends Fragment{
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

}
