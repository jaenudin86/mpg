package com.a.b.mileagetracker.DataAccess;

import android.database.Cursor;

/**
 * Created by Andrew on 10/28/2015.
 */
public class DialogInterfaces {
    public interface DialogInterface{
        void pressInitialButtonAction();
        void openEditVehicleEntryFragment();
        void dismissDialogFragment(String tag);
//        void openVehicleListFragment();
        void updateSharedPrefsVehicles();
        void updateToolBarView();
    }
}

