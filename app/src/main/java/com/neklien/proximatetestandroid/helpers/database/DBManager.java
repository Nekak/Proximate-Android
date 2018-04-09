package com.neklien.proximatetestandroid.helpers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.neklien.proximatetestandroid.AppDelegate;
import com.neklien.proximatetestandroid.R;

import java.io.InputStream;

/**
 * Created by nekak on 07/04/18.
 */

public class DBManager extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BDProximate.db";

    private static DBManager managerInstance;
    private Context context;

    public static final String userTable = "user";
    public static final String sectionTable = "section";
    public static final String userSectionTable = "user_section";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    static synchronized public DBManager getManagerInstance() {
        if (managerInstance == null) {
            managerInstance = new DBManager(AppDelegate.getInstance().getApplicationContext());
        }
        return managerInstance;
    }

    /**
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            InputStream inputStream = this.context.getResources().openRawResource(R.raw.bd_proximate);

            byte[] b = new byte[inputStream.available()];

            inputStream.read(b);

            String instructions = new String(b);
            String[] instructionsArray = instructions.split(";");

            for (String instrunction : instructionsArray) {
                String finalInstrunction = instrunction.trim();

                if (finalInstrunction.length() > 0) {
                    finalInstrunction += ";";
                    sqLiteDatabase.execSQL(finalInstrunction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * @param table
     * @param recordIdCol
     * @param values
     * @return
     */
    public long insertRecord(String table, String recordIdCol, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long recordId = db.insert(table, recordIdCol, values);

        return recordId;
    }

    /**
     * @param table
     * @param values
     * @return
     */
    public long updateRecord(String table, String whereClause, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long recordId = db.update(table, values, whereClause, null);

        return recordId;
    }

    /**
     * @param tableName
     * @return
     */
    public boolean hasRecords(String tableName) {
        String query = "select * from " + tableName;
        Cursor cursor = this.executeQuery(query);

        boolean response = cursor.moveToNext();

        cursor.close();

        return response;
    }

    /**
     * @param tableName
     * @param columnName
     * @param columnValue
     * @return
     */
    public boolean recordExists(String tableName, String columnName, String columnValue) {
        String query = "select * from " + tableName + " where " + columnName + " = ?";
        Cursor cursor = this.executeQuery(query, columnValue);

        boolean response = cursor.moveToNext();

        cursor.close();

        return response;
    }

    /**
     * @param tableName
     * @param whereClause
     * @param whereClauseArgs
     * @return
     */
    public int deleteWithQuery(String tableName, String whereClause, String... whereClauseArgs) {
        return this.getWritableDatabase().delete(tableName, whereClause, whereClauseArgs);
    }

    /**
     * @param query
     * @param params
     * @return
     */
    public Cursor executeQuery(String query, String... params) {
        return this.getReadableDatabase().rawQuery(query, params);
    }

    public synchronized void beginTransaction() {
        getWritableDatabase().beginTransaction();
    }

    public synchronized void setTransactionSuccessful() {
        getWritableDatabase().setTransactionSuccessful();
    }

    public synchronized void endTransaction() {
        getWritableDatabase().endTransaction();
    }
}
