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
    public void createVehicle(String vehicle);
    public void addEntry(String vehicle, int miles, double gallons, double price, long date, String location);
    public Cursor getAllData();
    public Cursor getMilesQuantityPrice();
    public void deleteRecord(int entry);
    public void changeRecord(int id, String vehicle, int miles, double gallons, double price, int date);
    public Cursor getMilesColumn();
    public Cursor getSumGallons();
    public Cursor getQuantityColumn();
    public Double getTotalAmountSpent();
    int getLastDate();
}
