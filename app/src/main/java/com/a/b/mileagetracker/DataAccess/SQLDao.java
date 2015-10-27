package com.a.b.mileagetracker.DataAccess;

import android.database.Cursor;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by Andrew on 10/21/2015.
 */

/**
 * Persistence methods for a mileage entry
 */
public interface SQLDao {
    void createVehicleTable(int year, String make, String model, String vehicleKey);
    void addEntry(int miles, double gallons, double price, long date, String location);
    Cursor getAllData();
    void changeRecord(int id, String vehicle, int miles, double gallons, double price, int date);
    Cursor getMilesColumn();
    Cursor getSumGallons();
    Cursor getQuantityColumn();
    Double getTotalAmountSpent();
    int getLastDate();
    boolean keyTableHasData();
    Cursor getAllDataFromKeyTable();
}