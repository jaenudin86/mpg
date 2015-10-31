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
    public static final String COLUMN_VEHICLE ="vehicle";
    public static final String COLUMN_MILEAGE = "mileage";
    public static final String COLUMN_QUANTITY= "quantity";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION = "location";
    private static final String DATABASE_NAME = "GasAppDB";
    private static final int DATABASE_VERSION = 36;
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
        Log.e("MySQLiteHelper called", "MySQLiteHelper called onCreate()");
        try {
            database.execSQL(KEY_DB_CREATE);
//            database.execSQL(DATABASE_CREATE);
//            addEntry("test Vehicle", 8500, 12.546, 34.56, 1443111100, "Chevron");
//            addEntry("test Vehicle2", 8600, 2.546, 6.56, 1443715900, "Texaco");
//            addEntry("test Vehicle3", 8600, 2.546, 7.56, 1444579900, "Exxon Mobil");
            Log.e("Created db", "Created db");
        } catch (SQLException e) {
            Log.e("failed to creat db", "failed to create db");
        }
        Log.e("created db sucessfully", "created db sucessfully!!!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cd=db.rawQuery("SELECT "+KEY_COLUMN_TABLE+"  FROM " + KEY_TABLE_NAME, null);
        while(cd.moveToNext()) {
            Log.e("dropping table", "dropped table " + cd.getString(0));
            db.execSQL("DROP TABLE IF EXISTS " + cd.getString(0));
        }cd.close();

        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + KEY_TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String location) {
        new LoadThread(location).start();
    }

    @Override
    public void createVehicleTable(int year, String make, String model, String vehicleKey) {
        try {
            mDb.execSQL("create table "
                    +vehicleKey+"("
                    +"_id INTEGER "+
                    "PRIMARY KEY AUTOINCREMENT, "
                    +COLUMN_VEHICLE+" TEXT, "
                    +COLUMN_MILEAGE+" INTEGER, "
                    +COLUMN_QUANTITY+" FLOAT, "
                    +COLUMN_PRICE+" FLOAT, "
                    +COLUMN_DATE+" INTEGER, "
                    +COLUMN_LOCATION+" TEXT )");
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

        ContentValues values = new ContentValues();
        values.put(COLUMN_MILEAGE, miles);
        values.put(COLUMN_QUANTITY, gallons);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_LOCATION, location);

        if(currentVehicle!=null){
            mDb.insert(currentVehicle, COLUMN_LOCATION, values);
        }else{
            Log.e("SP","Shared preferences doesn't have current vehicle selected");
        }
    }

    @Override
    public void deleteEntry(long position) {
        String pos=Long.toString(position);
        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        mDb=getWritableDatabase();
        int result=mDb.delete(currentVehicle,"_id = ?", new String[] {pos});
        Log.e("result","delete entry results ==> "+result+" for: "+currentVehicle+" at position: "+pos);
        getAllData(); //<--for testing only

    }

    @Override
    public Cursor getAllData() {
        mDb=getReadableDatabase();
        try {
            currentVehicle=mSharedPrefs.getString("currentVehicle",null);
            if(currentVehicle!=null){
                Log.e("cv", "current vehicle: "+currentVehicle);
                Cursor c=mDb.rawQuery("SELECT * FROM "+ currentVehicle +" ORDER BY "+COLUMN_DATE+" DESC", null);
                String names="";
                for(String s: c.getColumnNames()){
                    names=names+s+", ";
                }
                logger("getAllData() method in MySqlHelper: ---start---");
                Log.e("names", "column names: " + names);

                if(c.moveToFirst()){
                    String record="";
                    do{
                        record= String.format("%d _id , vehicle: %s, %d miles, %.3f gallons, %.2f dollars, date: %d, location %s",
                                c.getInt(0),
                                c.getString(1),
                                c.getInt(2),
                                c.getFloat(3),
                                c.getFloat(4),
                                c.getInt(5),
                                c.getString(6));

                        Log.e("record", "record: "+record);

                    }while(c.moveToNext());
                    logger("getAllData() method in MySqlHelper: ---end---");
                }
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Cursor getMilesColumn() {
        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        Cursor c=mDb.query(currentVehicle, new String[] {COLUMN_MILEAGE}, null, null, null, null, null, null);

        return c;
    }

    @Override
    public Cursor getSumGallons() {
        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        Cursor c = mDb.rawQuery("SELECT SUM (" + COLUMN_QUANTITY + ") FROM " + currentVehicle, null);
        c.moveToFirst();
        if(c.getDouble(0)>0) {
            return c;
        }else{
            return null;
        }
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

    @Override
    public int getLastDate() {
        currentVehicle=mSharedPrefs.getString("currentVehicle","null");
        Cursor c=mDb.rawQuery("SELECT MIN("+COLUMN_DATE+") FROM "+ currentVehicle,null);
        c.moveToFirst();
        Log.e("date from last date: ","date from last date: " + (Integer.parseInt(c.getString(0))));
        return Integer.parseInt(c.getString(0));
    }

    @Override
    public boolean keyTableHasData() {
        Cursor c=mDb.rawQuery("SELECT * FROM "+KEY_TABLE_NAME,null);
        return c.getCount()>0? true:false;
    }

    @Override
    public Cursor getAllDataFromKeyTable() {
        mDb = getWritableDatabase();
        Cursor c=mDb.rawQuery("SELECT * FROM "+KEY_TABLE_NAME,null);
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

