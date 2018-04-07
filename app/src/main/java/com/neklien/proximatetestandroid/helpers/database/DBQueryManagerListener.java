package com.neklien.proximatetestandroid.helpers.database;

/**
 * Created by nekak on 07/04/18.
 */

public interface DBQueryManagerListener {
    void onQueryResult(long resultCode, DBElement dbElement);
}
