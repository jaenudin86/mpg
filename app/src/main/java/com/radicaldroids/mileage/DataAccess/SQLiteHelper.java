package com.radicaldroids.mileage.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.radicaldroids.mileage.Constants;
import com.radicaldroids.mileage.R;

import java.text.DecimalFormat;

/**
 * Created by Andrew on 10/12/2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper singleton =null;
    public SQLiteDatabase mDb;
    private SharedPreferences mSharedPrefs;
    private Context mContext;
    public String currentVehicle="";

    public static final String KEY_COLUMN_YEAR="key_year";
    public static final String KEY_COLUMN_MAKE="key_make";
    public static final String KEY_COLUMN_MODEL="key_model";
    public static final String KEY_COLUMN_TABLE="key_table";

    public static final String KEY_TABLE_NAME = "vehicle_key_table";
    public static final String COLUMN_ID="_id";
    public static final String COLUMN_VEHICLE ="vehicle";
    public static final String COLUMN_ODOMETER = "odometer";
    public static final String COLUMN_QUANTITY= "quantity";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_MPG="MPG";
    private static final String DATABASE_NAME = "GasAppDB";
    private static final int DATABASE_VERSION = 39;

    private static final String KEY_DB_CREATE="create table "
            + KEY_TABLE_NAME +" ("
            +"_id INTEGER "+
            "PRIMARY KEY AUTOINCREMENT, "
            +KEY_COLUMN_YEAR+" INTEGER, "
            +KEY_COLUMN_MAKE+" TEXT, "
            +KEY_COLUMN_MODEL+" TEXT, "
            +KEY_COLUMN_TABLE+" TEXT )";

    String TAG="MySQLHelper";

    public static synchronized SQLiteHelper getInstance(Context context){
        if(singleton==null){
            singleton =new SQLiteHelper(context.getApplicationContext());
        }
        return singleton;
    }

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext=context;
        mSharedPrefs=this.mContext.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    /**
     * onCreate is called only when the database is created which is only created when the database number changes
     * @param database
     */

    @Override
    public void onCreate(SQLiteDatabase database) {
        mDb=database;
        try {
            database.execSQL(KEY_DB_CREATE);
        } catch (SQLException e) {
//            Log.e(TAG, "failed to create db");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cd=db.rawQuery("SELECT "+KEY_COLUMN_TABLE+"  FROM " + KEY_TABLE_NAME, null);
        while(cd.moveToNext()) {
//            Log.e(TAG, "dropped table " + cd.getString(0));
            db.execSQL("DROP TABLE IF EXISTS " + cd.getString(0));
        }cd.close();

//        Log.e(SQLiteHelper.class.getName(),"Upgrading database from version " + oldVersion + " to "+ newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + KEY_TABLE_NAME);
        onCreate(db);
    }

    public void createVehicleTable(int year, String make, String model, String vehicleKey) {
        mDb=getWritableDatabase();
        Log.e("SqliteHelper","DB: "+mDb);
        try {
            mDb.execSQL("create table "
                +vehicleKey+"("
                +COLUMN_ID+" INTEGER "+
                "PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_VEHICLE+" TEXT, "
                +COLUMN_ODOMETER +" INTEGER, "
                +COLUMN_QUANTITY+" FLOAT, "
                +COLUMN_PRICE+" FLOAT, "
                +COLUMN_DATE+" INTEGER, "
                +COLUMN_LOCATION+" TEXT, "
                +COLUMN_MPG+" FLOAT )");
            addVehicleToKeyTable(year, make, model, vehicleKey);
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(mContext.getApplicationContext(), R.string.invalid_make_or_type,Toast.LENGTH_LONG).show();
        }
    }
    public void addVehicleToKeyTable(int year, String make, String model, String vehicleKey){
        ContentValues values = new ContentValues();
        values.put(KEY_COLUMN_YEAR, year);
        values.put(KEY_COLUMN_MAKE, make);
        values.put(KEY_COLUMN_MODEL, model);
        values.put(KEY_COLUMN_TABLE, vehicleKey);
        mDb.insert(KEY_TABLE_NAME, KEY_COLUMN_MAKE, values);
    }

    private String getCurrentVehicle(){
        return mSharedPrefs.getString(Constants.SHARED_PREFS_CURRENT_VEHICLE,"null");
    }

    public void calculateMpgColumn() {
        mDb=getReadableDatabase();
        currentVehicle=getCurrentVehicle();
        String[] columns={COLUMN_ID, COLUMN_ODOMETER, COLUMN_QUANTITY};
        Cursor c=mDb.query(currentVehicle,columns,null,null,null,null,COLUMN_DATE+" DESC");
        if(c.getCount()>0) {
            c.moveToFirst();
            do {
                String id=c.getString(c.getColumnIndex(COLUMN_ID));
                int currentMiles=c.getInt(c.getColumnIndex(COLUMN_ODOMETER));
                Double currentGallons=c.getDouble(c.getColumnIndex(COLUMN_QUANTITY));
                int previousMiles= 0;
                c.moveToNext();
                try {
                    previousMiles = c.getInt(c.getColumnIndex(COLUMN_ODOMETER));
                } catch (Exception e) {
                    previousMiles=0;
                    currentMiles=0;
                }
                c.moveToPrevious();
                Double currentMpg=(currentMiles-previousMiles)/currentGallons;
//                Log.e(TAG, "calculate... miles:"+currentMiles+" - previous Miles: " +previousMiles+" / currentGallons: "+currentGallons+" equals: "+ currentMpg);

                DecimalFormat df3=new DecimalFormat("#.###");

                ContentValues cv=new ContentValues();
                cv.put(COLUMN_MPG, Double.valueOf(df3.format(currentMpg)));
                String [] args={id};
                mDb.update(currentVehicle,cv,"_id=?",args);

            } while (c.moveToNext());
        }
    }
}