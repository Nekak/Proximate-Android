package com.neklien.proximatetestandroid.helpers.database;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by nekak on 07/04/18.
 */

public class DBQueryManager {
    public static void saveUser(Context context, User user, DBQueryManagerListener queryManagerListener) {
        synchronized (context) {
            long result = persistDBElement(user, "userId", user.getIdUser(), DBManager.userTable);

            if (result < 0) {
                //Error
            } else {
                queryManagerListener.onQueryResult(result, user);
            }
        }
    }

    public static void deleteUser(DBQueryManagerListener queryManagerListener) {
        long result = -1;

        Cursor c = DBManager.getManagerInstance().executeQuery("select * from user;");

        while (c.moveToNext()) {
            long idUser = c.getLong(c.getColumnIndex("userId"));

            DBManager.getManagerInstance().beginTransaction();

            if (DBManager.getManagerInstance().recordExists(DBManager.userTable, "userId", String.valueOf(idUser))) {
                // -1 error
                result = DBManager.getManagerInstance().deleteWithQuery(DBManager.userTable, "userId = ?", String.valueOf(idUser));
            }

            DBManager.getManagerInstance().setTransactionSuccessful();
            DBManager.getManagerInstance().endTransaction();
        }

        c.close();

        queryManagerListener.onQueryResult(result, null);
    }

    public static User getUser() {
        Cursor c = DBManager.getManagerInstance().executeQuery("select * from " + DBManager.userTable + ";");

        User user = null;

        while (c.moveToNext()) {
            User row = new User();
            row.initWithCursor(c);
            user = row;
            break;
        }

        c.close();

        return user;
    }

    private static long persistDBElement(DBElement element, String idColumn, long idElement, String tableName) {
        long result = -1;

        DBManager.getManagerInstance().beginTransaction();

        if (!DBManager.getManagerInstance().recordExists(tableName, idColumn, String.valueOf(idElement))) {
            // -1 error
            result = DBManager.getManagerInstance().insertRecord(tableName, idColumn, element.getContentValue());
        } else {
            // 0 no update
            result = DBManager.getManagerInstance().updateRecord(tableName, idColumn + "=" + String.valueOf(idElement), element.getContentValue());

            if (result == 1) {
                result++;
            }

            // -1 error
            result--;
        }

        DBManager.getManagerInstance().setTransactionSuccessful();
        DBManager.getManagerInstance().endTransaction();

        return result;
    }
}
