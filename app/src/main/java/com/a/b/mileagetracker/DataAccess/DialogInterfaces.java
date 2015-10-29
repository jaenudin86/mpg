package com.a.b.mileagetracker.DataAccess;

/**
 * Created by Andrew on 10/28/2015.
 */
public class DialogInterfaces {
    public interface DialogInterface{
        void onDialogAddEntryDismiss();
        void onDialogAddVehicleDismiss();
        void openEditVehicleEntryFragment();
        void onEditDate();
        void selectCurrentCar(String make, String model, int year);
    }
}
