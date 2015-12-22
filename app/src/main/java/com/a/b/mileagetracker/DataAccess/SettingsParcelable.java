package com.a.b.mileagetracker.DataAccess;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andrew on 12/21/2015.
 */
public class SettingsParcelable implements Parcelable {
    private int mVehicles;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    public static final Parcelable.Creator<SettingsParcelable> CREATOR
            = new Parcelable.Creator<SettingsParcelable>() {
        public SettingsParcelable createFromParcel(Parcel in) {
            return new SettingsParcelable(in);
        }

        public SettingsParcelable[] newArray(int size) {
            return new SettingsParcelable[size];
        }
    };

    private SettingsParcelable(Parcel in) {
        mVehicles = in.readInt();
    }
}
