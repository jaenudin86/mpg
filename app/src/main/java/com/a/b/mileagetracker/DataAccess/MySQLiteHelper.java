package com.a.b.mileagetracker.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by Andrew on 10/12/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper implements SQLDao{

    private static MySQLiteHelper singleton =null;
    public SQLiteDatabase mDb;
    private SharedPreferences mSharedPrefs;
    private Context mContext;
    public String currentVehicle="";

    public static final String KEY_COLUMN_YEAR="key_year";
    public static final String KEY_COLUMN_MAKE="key_make";
    public static final String KEY_COLUMN_MODEL="key_model";
    public static final String KEY_COLUMN_TABLE="key_table";

    public static final String KEY_TABLE_NAME = "vehicle_key_table";
//    public static final String KEY_TABLE_NAME = "fillupTable";
    public static final String COLUMN_ID="_id";
    public static final String COLUMN_VEHICLE ="Vehicle";
    public static final String COLUMN_MILEAGE = "mileage";
    public static final String COLUMN_QUANTITY= "quantity";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_MPG="MPG";
    private static final String DATABASE_NAME = "GasAppDB";
    private static final int DATABASE_VERSION = 37;
    private static final String KEY_DB_CREATE="create table "
            + KEY_TABLE_NAME +" ("
            +"_id INTEGER "+
            "PRIMARY KEY AUTOINCREMENT, "
            +KEY_COLUMN_YEAR+" INTEGER, "
            +KEY_COLUMN_MAKE+" TEXT, "
            +KEY_COLUMN_MODEL+" TEXT, "
            +KEY_COLUMN_TABLE+" TEXT )";
//    private static final String DATABASE_CREATE="create table "
//            +KEY_TABLE_NAME+" ("
//            +"_id INTEGER "+
//            "PRIMARY KEY AUTOINCREMENT, "
//            +COLUMN_VEHICLE+" TEXT, "
//            +COLUMN_MILEAGE+" INTEGER, "
//            +COLUMN_QUANTITY+" FLOAT, "
//            +COLUMN_PRICE+" FLOAT, "
//            +COLUMN_DATE+" INTEGER, "
//            +COLUMN_LOCATION+" TEXT )";
//    private static final String INSERT_QUERY=
//            "INSERT INTO "+KEY_TABLE_NAME+" "+
//                    "("+COLUMN_QUANTITY+", "+COLUMN_MILEAGE+", "+COLUMN_LOCATION+") "+
////                    "(quantity,mileage,location) "+
//                    "VALUES "+
//                    "(12.4,174015,'Chevron'), "+
//                    "(17.4,174529,'Texaco')";
    String TAG="MySQLHelper";

    public static synchronized MySQLiteHelper getInstance(Context context){
        if(singleton==null){
            singleton =new MySQLiteHelper(context.getApplicationContext());
        }
        return singleton;
    }

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext=context;
        mSharedPrefs=this.mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    /**
     * onCreate is called only when the database is created which is only created when the database number changes
     * @param database
     */

    @Override
    public void onCreate(SQLiteDatabase database) {
//        if (db == null) {
//            db = getWritableDatabase();
//        }
        mDb=database;
        Log.e(TAG, "MySQLiteHelper called onCreate()");
        try {
            database.execSQL(KEY_DB_CREATE);
//            Attempting to fill the first row in vehicle key table; hoping to create a title for spinner when first starting app
//            ContentValues values = new ContentValues();
//            values.put(KEY_COLUMN_MAKE, "Mileage Tracker");
//            mDb.insert(KEY_TABLE_NAME, KEY_COLUMN_MAKE, values);
        } catch (SQLException e) {
            Log.e(TAG, "failed to create db");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cd=db.rawQuery("SELECT "+KEY_COLUMN_TABLE+"  FROM " + KEY_TABLE_NAME, null);
        while(cd.moveToNext()) {
            Log.e(TAG, "dropped table " + cd.getString(0));
            db.execSQL("DROP TABLE IF EXISTS " + cd.getString(0));
        }cd.close();

        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + KEY_TABLE_NAME);
        onCreate(db);
    }

//    public void insertData(String location) {
//        new LoadThread(location).start();
//    }

    @Override
    public void createVehicleTable(int year, String make, String model, String vehicleKey) {
        try {
            mDb.execSQL("create table "
                    +vehicleKey+"("
                    +COLUMN_ID+" INTEGER "+
                    "PRIMARY KEY AUTOINCREMENT, "
                    +COLUMN_VEHICLE+" TEXT, "
                    +COLUMN_MILEAGE+" INTEGER, "
                    +COLUMN_QUANTITY+" FLOAT, "
                    +COLUMN_PRICE+" FLOAT, "
                    +COLUMN_DATE+" INTEGER, "
                    +COLUMN_LOCATION+" TEXT, "
                    +COLUMN_MPG+" FLOAT )");
            addVehicleToKeyTable(year, make, model, vehicleKey);
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(mContext.getApplicationContext(),"Invalid vehicle make or type",Toast.LENGTH_LONG).show();
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
    public String cleanString(String input){
        return input.replaceAll("\\s","");
    }

    @Override
    public void addEntry(int miles, double gallons, double price, long date, String location) {

        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        logger("MySQLiteHelper.java addEntry: miles: " + miles + ", gals: " + gallons + ", $" + price + ", date: " + date + ", location: " + location);

        if(currentVehicle!=null){
            ContentValues values = new ContentValues();
            values.put(COLUMN_VEHICLE, currentVehicle);
            values.put(COLUMN_MILEAGE, miles);
            values.put(COLUMN_QUANTITY, gallons);
            values.put(COLUMN_PRICE, price);
            values.put(COLUMN_DATE, date);
            values.put(COLUMN_LOCATION, location);

            mDb.insert(currentVehicle, COLUMN_LOCATION, values);
        }else{
            Log.e(TAG,"Shared preferences doesn't have current vehicle selected");
        }
    }

    @Override
    public void deleteEntry(long position) {
        String pos=Long.toString(position);
        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        mDb=getWritableDatabase();
        int result=mDb.delete(currentVehicle,"_id = ?", new String[] {pos});
        Log.e(TAG, "delete entry results ==> " + result + " for: " + currentVehicle + " at position: " + pos);
        getAllData(); //<--for testing only
    }

    @Override
    public void deleteVehicle(String vehicle) {
        Log.e(TAG, "before removing from key table------:");
        logKeyTable();

        String[] pos= new String[]{vehicle};
        mDb.delete(KEY_TABLE_NAME, KEY_COLUMN_TABLE + " = ?", pos);
        mDb.execSQL("DROP TABLE IF EXISTS " + vehicle);

        Log.e(TAG, "after removing from key table------:");
        logKeyTable();
    }
    public void logKeyTable(){
        Cursor c=getAllDataFromKeyTable();
        if(c.moveToFirst()) {
            String record = "";
            do {
                record = String.format("%d _id , make: %s, model: %s, year: %s, associated name: %s",
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4));

                Log.e(TAG, "getAllDataFromKeyTable----: " + record);
            } while (c.moveToNext());
        }
    }

    @Override
    public Cursor getAllData() {
        mDb=getReadableDatabase();
        try {
            currentVehicle=mSharedPrefs.getString("currentVehicle",null);
            if(currentVehicle!=null){
                Cursor c=mDb.rawQuery("SELECT * FROM "+ currentVehicle +" ORDER BY "+COLUMN_DATE+" DESC", null);
                String names="";
                for(String s: c.getColumnNames()){
                    names=names+s+", ";
                }
                logger("getAllData() method in MySqlHelper: ---start---");
                Log.e(TAG, "column names: " + names);

//                if(c.moveToFirst()){
//                    String record="";
//                    do{
//                        record= String.format("%d _id , vehicle: %s, %d miles, %.3f gallons, %.2f dollars, date: %d, location %s, MPG %.3f",
//                                c.getInt(0),
//                                c.getString(1),
//                                c.getInt(2),
//                                c.getFloat(3),
//                                c.getFloat(4),
//                                c.getInt(5),
//                                c.getString(6),
//                                c.getFloat(7));
//
//                        Log.e(TAG, "record: "+record);
//
//                    }while(c.moveToNext());
//                    logger("getAllData() method in MySqlHelper: ---end---");
//                }
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Cursor getAllData(String vehicle) {
        mDb=getReadableDatabase();
        try {
            if(vehicle!=null){
                Cursor c= null;
                try {
                    c = mDb.rawQuery("SELECT * FROM "+ vehicle +" ORDER BY "+COLUMN_DATE+" DESC", null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void calculateMpgColumn() {
        currentVehicle=mSharedPrefs.getString("currentVehicle", "null");
        String[] columns={COLUMN_ID,COLUMN_MILEAGE, COLUMN_QUANTITY};
        Cursor c=mDb.query(currentVehicle,columns,null,null,null,null,COLUMN_DATE+" DESC");
        if(c.getCount()>0) {
            c.moveToFirst();
            do {
                String id=c.getString(c.getColumnIndex(COLUMN_ID));
                int currentMiles=c.getInt(c.getColumnIndex(COLUMN_MILEAGE));
                Double currentGallons=c.getDouble(c.getColumnIndex(COLUMN_QUANTITY));
                int previousMiles= 0;
                c.moveToNext();
                try {
                    previousMiles = c.getInt(c.getColumnIndex(COLUMN_MILEAGE));
                } catch (Exception e) {
                    previousMiles=0;
                    currentMiles=0;
                }
                c.moveToPrevious();
                Double currentMpg=(currentMiles-previousMiles)/currentGallons;
                Log.e(TAG, "calculate... miles:"+currentMiles+" - previous Miles: " +previousMiles+" / currentGallons: "+currentGallons+" equals: "+ currentMpg);

                DecimalFormat df3=new DecimalFormat("#.###");

                ContentValues cv=new ContentValues();
                cv.put(COLUMN_MPG, Double.valueOf(df3.format(currentMpg)));
                String [] args={id};
                mDb.update(currentVehicle,cv,"_id=?",args);

            } while (c.moveToNext());
        }
    }

    @Override
    public Cursor getMilesColumn() {
        currentVehicle=mSharedPrefs.getString("currentVehicle", "null");
        Cursor c=mDb.query(currentVehicle, new String[] {COLUMN_MILEAGE}, null, null, null, null, null, null);
        return c;
    }

    @Override
    public Cursor getSumGallons() {
        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        return mDb.rawQuery("SELECT SUM (" + COLUMN_QUANTITY + ") FROM " + currentVehicle, null);
//        c.moveToFirst();
//        if(c.getDouble(0)>0) {
//            return c;
//        }else{
//            return null;
//        }
    }

    @Override
    public Cursor getQuantityColumn() {
        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        Cursor c = mDb.query(currentVehicle, new String[]{COLUMN_QUANTITY},null,null,null,null,null);
        return c;
    }

    @Override
    public Double getTotalAmountSpent() {
        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        Cursor cPrice=mDb.rawQuery("SELECT SUM (" + COLUMN_PRICE + ") FROM " + currentVehicle, null);
        cPrice.moveToFirst();
        if(cPrice.getDouble(0)>0) {
            DecimalFormat df = new DecimalFormat("#.##");
            return Double.parseDouble(df.format(Double.parseDouble(cPrice.getString(0))));
        }else{
            return 0.0;
        }
    }

//    @Override
//    public int getLastDate() {
//        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
//        Cursor c=mDb.rawQuery("SELECT MIN(" + COLUMN_DATE + ") FROM " + currentVehicle, null);
//        c.moveToFirst();
//        return Integer.parseInt(c.getString(0));
//    }

    @Override
    public boolean keyTableHasData() {
        Cursor c=mDb.rawQuery("SELECT * FROM " + KEY_TABLE_NAME, null);
        return c.getCount()>0? true:false;
    }

    @Override
    public Cursor getAllDataFromKeyTable() {
        mDb = getWritableDatabase();
        Cursor c=mDb.rawQuery("SELECT * FROM "+KEY_TABLE_NAME,null);
        return c;
    }

    @Override
    public Cursor getMpgColumn() {
        Cursor c=null;
        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        if (currentVehicle.compareToIgnoreCase("null")!=0) {
            try {
                c=mDb.query(currentVehicle, new String[]{COLUMN_MPG}, null, null, null, null, COLUMN_DATE + " DESC");
            } catch (Exception e) {
            }
        }else{
            return null;
        }
        return c;
    }

    @Override
    public void changeRecord(int id, String vehicle, int miles, double gallons, double price, int date) {
    }

    private class LoadThread extends Thread{
        private int position=-1;
        private String location;
        LoadThread(String location){
            super();
            this.location=location;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            String[] args={String.valueOf(position)};
            Cursor c=
                    getReadableDatabase().rawQuery("SELECT * FROM "+ KEY_TABLE_NAME +" WHERE position = ? ", args);
            c.getColumnCount();

            c.close();
        }
    }
    private void logger(String log){
        Log.e("DataDAOImplementation",log);
    }
    private class UpdateThread extends Thread{
    }
}

