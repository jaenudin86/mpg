package com.radicaldroids.mileage.DataAccess;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;

import com.radicaldroids.mileage.Constants;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Andrew on 11/6/2015.
 */
public class DataProvider extends ContentProvider {
    public static final String BASE_CONTENT_URI ="content://com.radicaldroids.mileage";
    public static final String AUTHORITY ="com.radicaldroids.mileage";
    private ContentResolver contentResolver;
    SQLiteHelper mDBHelper;
    String TAG="DataProvider class";

    private static UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY,"key_table", 1);
        sUriMatcher.addURI(AUTHORITY,"delete_vehicle", 10);
        sUriMatcher.addURI(AUTHORITY,"delete_entry", 11);
        sUriMatcher.addURI(AUTHORITY,"vehicle", 3);
        sUriMatcher.addURI(AUTHORITY,"mpg_data", 4);
        sUriMatcher.addURI(AUTHORITY,"sum_gals", 5);
        sUriMatcher.addURI(AUTHORITY,"current_vehicle", 6);
        sUriMatcher.addURI(AUTHORITY,"fillup", 7);
        sUriMatcher.addURI(AUTHORITY,"spreadsheet.xls",20);
    }

    @Override
    public boolean onCreate() {
        contentResolver=getContext().getContentResolver();
        mDBHelper = new SQLiteHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor=null;
        SQLiteHelper helper=new SQLiteHelper(getContext());
        final SQLiteDatabase db=helper.getWritableDatabase();

        SharedPreferences sharedPrefs=getContext().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        String currentVehicle= sharedPrefs.getString(Constants.SHARED_PREFS_CURRENT_VEHICLE,"null");

        switch(sUriMatcher.match(uri)){
            case 1:
                cursor=db.rawQuery("SELECT * FROM "+SQLiteHelper.KEY_TABLE_NAME,null);
                break;

            case 3:
                if(selection!=null) {
                    try {
                        cursor = db.rawQuery("SELECT * FROM " + selection + " ORDER BY " + SQLiteHelper.COLUMN_DATE + " DESC", null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                break;

            case 4:
                if(currentVehicle.compareToIgnoreCase("null")!=0){
                    try{
                        cursor=db.query(currentVehicle,new String[]{SQLiteHelper.COLUMN_MPG},null,null,null,null, SQLiteHelper.COLUMN_DATE+" DESC");
                    }catch (Exception e){
                    }
                }else{
                    cursor=null;
                }
                break;

            case 5:
                db.rawQuery("SELECT SUM (" + SQLiteHelper.COLUMN_QUANTITY + ") FROM " + currentVehicle, null);
                break;

            case 6:
                if(currentVehicle.compareToIgnoreCase("null")!=0){
                    try {
                        cursor = db.rawQuery("SELECT * FROM " + currentVehicle + " ORDER BY " + SQLiteHelper.COLUMN_DATE + " DESC", null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            default:
        }
        return cursor;
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {

        switch (sUriMatcher.match(uri)) {
            case 20:
                String fileLocation = getContext().getCacheDir()+File.separator+"mpgxls.xls";

                ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(
                        fileLocation), ParcelFileDescriptor.MODE_READ_ONLY);
                return pfd;

            default:
                throw new FileNotFoundException("Unsupported uri: "
                        + uri.toString());
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        if(uri.getLastPathSegment()==null){
            return "com.something.here.madeup";
        }else {
            return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SharedPreferences sharedPrefs=getContext().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        String currentVehicle= sharedPrefs.getString(Constants.SHARED_PREFS_CURRENT_VEHICLE,"null");

        SQLiteHelper helper=new SQLiteHelper(getContext());
        final SQLiteDatabase db=helper.getWritableDatabase();
        db.insert(currentVehicle,null,values);

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteHelper helper=new SQLiteHelper(getContext());
        final SQLiteDatabase db=helper.getWritableDatabase();
        int rowsDeleted=0;
        switch(sUriMatcher.match(uri)){

            case 10:    //delete vehicle
                String[] row= new String[]{selection};
                //delete vehicle from key table
                rowsDeleted=db.delete(SQLiteHelper.KEY_TABLE_NAME,SQLiteHelper.KEY_COLUMN_TABLE+" = ?",row);
                //drop the table for the selected vehicle
                db.execSQL("DROP TABLE IF EXISTS "+selection);
                break;

            case 11:    //delete single entry
                SharedPreferences sharedPrefs=getContext().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
                String currentVehicle= sharedPrefs.getString(Constants.SHARED_PREFS_CURRENT_VEHICLE,"null");

                rowsDeleted= db.delete(currentVehicle,"_id = ?", new String[]{selection});
                break;
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
