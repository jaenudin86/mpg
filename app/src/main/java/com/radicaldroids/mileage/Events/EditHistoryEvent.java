package com.radicaldroids.mileage.Events;

import android.database.Cursor;

/**
 * Created by Andrew on 10/29/2015.
 */
public class EditHistoryEvent {

    public final Cursor mCursor;
    public final int mPosition;
    public final long mId;

    public EditHistoryEvent(Cursor c, int position, long id) {
        this.mCursor = c;
        this.mPosition = position;
        this.mId = id;
    }
}
