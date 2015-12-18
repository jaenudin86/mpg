package com.a.b.mileagetracker.DataAccess;

import android.database.Cursor;

/**
 * Created by Andrew on 10/28/2015.
 */
public class DialogInterfaces {
    public interface DialogInterface{
        void pressInitialButtonAction();
        void onDialogAddVehicle();
        void onDialogAddVehicleDismiss(String tag);
        void openEditVehicleEntryFragment();
        void onEditDate();
        void selectCurrentCar(String make, String model, int year);
        void dismissDialogFragment(String tag);
        void openVehicleListFragment();
        void updateToolBarView();
        void updateSharedPrefsVehicles();
    }
}
