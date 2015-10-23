package com.a.b.mileagetracker.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.*;
import android.util.Log;

import java.lang.Process;
import java.text.DecimalFormat;

/**
 * Created by Andrew on 10/12/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper implements SQLDao{

    private static MySQLiteHelper singleton =null;
    private SQLiteDatabase db;

    public static final String TABLE_NAME = "fillupTable";
    public static final String COLUMN_VEHICLE ="vehicle";
    public static final String COLUMN_MILEAGE = "mileage";
    public static final String COLUMN_QUANTITY= "quantity";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION = "location";
    private static final String DATABASE_NAME = "GasAppDB";
    private static final int DATABASE_VERSION = 23;
//    private static final String DATABASE_CREATE = "create table "
//            + TABLE_NAME + "(" + COLUMN_ID
//            + " integer primary key autoincrement, " + COLUMN_MILEAGE+"varchar(7), "+COLUMN_QUANTITY+"varchar(7), "+COLUMN_LOCATION+"varchar(7))"
//            + " text not null);";
    private static final String DATABASE_CREATE="create table "
        +TABLE_NAME+" ("
        +"_id INTEGER "+
        "PRIMARY KEY AUTOINCREMENT, "
        +COLUMN_VEHICLE+" TEXT, "
        +COLUMN_MILEAGE+" INTEGER, "
        +COLUMN_QUANTITY+" FLOAT, "
        +COLUMN_PRICE+" FLOAT, "
        +COLUMN_DATE+" INTEGER, "
        +COLUMN_LOCATION+" TEXT )";
    private static final String INSERT_QUERY=
            "INSERT INTO "+TABLE_NAME+" "+
                    "("+COLUMN_QUANTITY+", "+COLUMN_MILEAGE+", "+COLUMN_LOCATION+") "+
//                    "(quantity,mileage,location) "+
                    "VALUES "+
                    "(12.4,174015,'Chevron'), "+
                    "(17.4,174529,'Texaco')";

    public static synchronized MySQLiteHelper getInstance(Context context){
        if(singleton==null){
            singleton =new MySQLiteHelper(context.getApplicationContext());
        }
        return singleton;
    }

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db=getWritableDatabase();
        Log.e("MySQLiteHelper called", "MySQLiteHelper called");
    }

    /**
     * onCreate is called only when the database is created which is only created when the database number changes
     * @param database
     */

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.e("MySQLiteHelper called","MySQLiteHelper called onCreate()");
        try {
            database.execSQL(DATABASE_CREATE);
            database.execSQL(INSERT_QUERY);
//            ContentValues cv = new ContentValues();
//            cv.put(COLUMN_LOCATION, "Chevron");
//            cv.put(COLUMN_MILEAGE, "172500");
//            cv.put(COLUMN_QUANTITY, "12.5");
//            database.insert(TABLE_NAME, COLUMN_LOCATION, cv);
            Log.e("Created db", "Created db");

        }catch (SQLException e){
            Log.e("failed to creat db","failed to create db");
        }
        Log.e("created db sucessfully","created db sucessfully!!!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String location) {
        new LoadThread(location).start();
    }

    @Override
    public void createVehicle(String vehicle) {

    }

    @Override
    public void addEntry(String vehicle, int miles, double gallons, double price, int date) {
//        dbHelper = MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        db=getWritableDatabase();
        logger("MySQLiteHelper.java addEntry: " + vehicle + ", " + miles + ", " + gallons + ", " + price + ", " + date);

        ContentValues values = new ContentValues();
        values.put(COLUMN_VEHICLE, vehicle);
        values.put(COLUMN_MILEAGE, miles);
        values.put(COLUMN_QUANTITY, gallons);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_LOCATION, "dummy");

        db.insert(TABLE_NAME, COLUMN_LOCATION, values);
    }

    @Override
    public Cursor getAllData() {
//        db=getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM fillupTable", null);
        String names="";
        for(String s: c.getColumnNames()){
            names=names+s+", ";
        }
        Log.e("names", "column names: " + names);

        if(c.moveToFirst()){
            String record="";
            do{
                record= String.format("%d , vehicle: %s, %d miles, %.3f gallons, %.2f dollars, location %s",
                        c.getInt(0),
                        c.getString(1),
                        c.getInt(2),
                        c.getFloat(3),
                        c.getFloat(4),
                        c.getString(5));

                Log.e("record", "record: "+record);

            }while(c.moveToNext());
        }
        return c;
    }

    @Override
    public Cursor getMilesQuantityPrice() {
//        Cursor c = db.rawQuery("SELECT * FROM fillupTable", null);
        Cursor c=db.query(TABLE_NAME, new String[]{COLUMN_MILEAGE, COLUMN_QUANTITY, COLUMN_PRICE}, null, null, null, null, null, null);

        return c;
    }
    @Override
    public Cursor getMilesColumn() {
        Cursor c=db.query(TABLE_NAME, new String[] {COLUMN_MILEAGE}, null, null, null, null, null, null);

        return c;
    }

    @Override
    public Cursor getSumGallons() {
        Cursor c=db.rawQuery("SELECT SUM ("+COLUMN_QUANTITY+") FROM "+TABLE_NAME,null);
        Log.e("getsum","getsumgallons: "+c.getColumnName(0));
        return c;
    }

    @Override
    public Cursor getQuantityColumn() {
        Cursor c = db.query(TABLE_NAME, new String[]{COLUMN_QUANTITY},null,null,null,null,null);
        return c;
    }

    @Override
    public Double getTotalAmountSpent() {
        Cursor cPrice=db.rawQuery("SELECT SUM (" + COLUMN_PRICE + ") FROM "+TABLE_NAME, null);
        DecimalFormat df=new DecimalFormat("#.##");
        cPrice.moveToFirst();
        return Double.parseDouble(df.format(Double.parseDouble(cPrice.getString(0))));
    }

    @Override
    public void deleteRecord(int entry) {

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
                    getReadableDatabase().rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE position = ? ", args);
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

