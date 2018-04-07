package com.neklien.proximatetestandroid.helpers.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by nekak on 07/04/18.
 */

public interface DBElement {
    void initWithCursor(Cursor c);
    ContentValues getContentValue();
}
