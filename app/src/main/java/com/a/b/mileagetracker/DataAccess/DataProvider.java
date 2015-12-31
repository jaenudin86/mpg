package com.a.b.mileagetracker.DataAccess;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Andrew on 11/6/2015.
 */
public class DataProvider extends ContentProvider {
    public static final String AUTHORITY="content://com.a.b.mileagetracker";
    public static final Uri CONTENT_URI=Uri.parse(AUTHORITY);
    private ContentResolver contentResolver;
    MySQLiteHelper mDBHelper;
//    SQLiteDatabase mSqliteDB;
    SQLDao mSQLDao;
    String TAG="DataProvider class";

    private static UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI("com.a.b.mileagetracker", "key_table", 1);
        sUriMatcher.addURI("com.a.b.mileagetracker", "delete_vehicle", 10);
        sUriMatcher.addURI("com.a.b.mileagetracker", "vehicle", 3);
        sUriMatcher.addURI("com.a.b.mileagetracker","mpg_data", 4);
        sUriMatcher.addURI("com.a.b.mileagetracker","sum_gals", 5);
        sUriMatcher.addURI("com.a.b.mileagetracker","spreadsheet.xls",20);
    }

    @Override
    public boolean onCreate() {
        contentResolver=getContext().getContentResolver();
        mDBHelper = new MySQLiteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    /**
     * 1. A Uri representing the collection or instance being queried
     2. A String array representing the list of properties that should be returned
     3. A String representing what amounts to a SQL WHERE clause, constraining
     which instances should be considered for the query results
     4. A String array representing values to “pour into” the WHERE clause, replacing
     any ? found there
     5. A String representing what amounts to a SQL ORDER BY clause
     */
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c=null;
        mDBHelper=MySQLiteHelper.getInstance(getContext());
        switch(sUriMatcher.match(uri)){
            case 1:
                c=mDBHelper.getAllDataFromKeyTable();
                break;
            case 2:
                break;
            case 3:
                c=mDBHelper.getAllData(selection);
                break;
            case 4:
                c=mDBHelper.getMpgColumn();
                break;
            case 5:
                c=mDBHelper.getSumGallons();
                break;
            default:
        }

//        mDBHelper=MySQLiteHelper.getInstance(getContext());
//        Cursor c=mSQLDao.getAllData();
        return c;
    }

        @Nullable
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {

//        Log.e(TAG,"ContentProvider openFile method called with uri: '" + uri + "'." + uri.getLastPathSegment());

        // Check incoming Uri against the matcher
        switch (sUriMatcher.match(uri)) {
            // If it returns 1 - then it matches the Uri defined in onCreate
            case 20:
                // The desired file name is specified by the last segment of the
                // path
                // E.g.
                // 'content://com.stephendnicholas.gmailattach.provider/Test.txt'
                // Take this and build the path to the file
                String fileLocation = getContext().getCacheDir() +File.separator+"mpgxls.xls";
//                Log.e(TAG,"ContentProvider openFile method file location: "+fileLocation);

                // Create & return a ParcelFileDescriptor pointing to the file
                // Note: I don't care what mode they ask for - they're only getting
                // read only
                ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(
                        fileLocation), ParcelFileDescriptor.MODE_READ_ONLY);
                return pfd;

            // Otherwise unrecognised Uri
            default:
//                Log.e(TAG, "Unsupported uri: '" + uri + "'.");
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
//        long id=mSqliteDB.insertWithOnConflict(table,null,values,SQLiteDatabase.CONFLICT_IGNORE)
//        if(id!=-1){
//            return Uri.withAppendedPath(uri,Long.toString(id));
//        }else{
            return uri;
//        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(sUriMatcher.match(uri)){
            case 10:
                mDBHelper.deleteVehicle(selection);
                break;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
