package com.neklien.proximatetestandroid.helpers.database;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by nekak on 07/04/18.
 */

public class DBQueryManager {
    public static void saveUser(Context context, User user, DBQueryManagerListener queryManagerListener) {
        synchronized (context) {
            long result = persistDBElement(user, "id_user", user.getIdUser(), DBManager.userTable);

            if (result < 0) {
                //Error
            } else {
                queryManagerListener.onQueryResult(result, user);
            }
        }
    }

    public static void saveSection(Context context, Section section, DBQueryManagerListener queryManagerListener) {
        synchronized (context) {
            long result = persistDBElement(section, "id_section", section.getIdSection(), DBManager.sectionTable);

            if (result < 0) {
                //Error
            } else {
                queryManagerListener.onQueryResult(result, section);
            }
        }
    }

    public static long findIdForUserSection(long idUser, long idSection) {
        long idSP = -1;

        Cursor c = DBManager.getManagerInstance().executeQuery("select * from user_section where id_user = " + idUser + " and id_section = " + idSection + ";");

        while (c.moveToNext()) {
            idSP = c.getLong(c.getColumnIndex("id_user_section"));
            break;
        }

        c.close();

        return idSP;
    }

    public static long getLastUserSectionIndex() {
        Cursor c = DBManager.getManagerInstance().executeQuery("select * from " + DBManager.userSectionTable + " order by id_user_section asc;");

        long idUS = 0;

        while (c.moveToNext()) {
            idUS = c.getLong(c.getColumnIndex("id_user_section"));
            //break;
        }

        c.close();

        return idUS;
    }

    public static void saveUserSection(Context context, UserSection userSection, DBQueryManagerListener queryManagerListener) {
        synchronized (context) {
            long result = persistDBElement(userSection, "id_user_section", userSection.getIdUserSection(), DBManager.userSectionTable);

            if (result < 0) {

            }

            queryManagerListener.onQueryResult(result, userSection);
        }
    }

    public static void deleteUser(DBQueryManagerListener queryManagerListener) {
        long result = -1;

        Cursor c = DBManager.getManagerInstance().executeQuery("select * from user;");

        while (c.moveToNext()) {
            long idUser = c.getLong(c.getColumnIndex("id_user"));

            DBManager.getManagerInstance().beginTransaction();

            if (DBManager.getManagerInstance().recordExists(DBManager.userTable, "id_user", String.valueOf(idUser))) {
                // -1 error
                result = DBManager.getManagerInstance().deleteWithQuery(DBManager.userTable, "id_user = ?", String.valueOf(idUser));
            }

            DBManager.getManagerInstance().setTransactionSuccessful();
            DBManager.getManagerInstance().endTransaction();
        }

        c.close();

        queryManagerListener.onQueryResult(result, null);
    }

    public static void deleteSection(DBQueryManagerListener queryManagerListener) {
        long result = -1;

        Cursor c = DBManager.getManagerInstance().executeQuery("select * from section;");

        while (c.moveToNext()) {
            long idSection = c.getLong(c.getColumnIndex("id_section"));

            DBManager.getManagerInstance().beginTransaction();

            if (DBManager.getManagerInstance().recordExists(DBManager.sectionTable, "id_section", String.valueOf(idSection))) {
                // -1 error
                result = DBManager.getManagerInstance().deleteWithQuery(DBManager.sectionTable, "id_section = ?", String.valueOf(idSection));
            }

            DBManager.getManagerInstance().setTransactionSuccessful();
            DBManager.getManagerInstance().endTransaction();
        }

        c.close();

        queryManagerListener.onQueryResult(result, null);
    }

    public static void deleteUserSection(DBQueryManagerListener queryManagerListener) {
        long result = -1;

        Cursor c = DBManager.getManagerInstance().executeQuery("select * from user_section;");

        while (c.moveToNext()) {
            long idUS = c.getLong(c.getColumnIndex("id_user_section"));

            DBManager.getManagerInstance().beginTransaction();

            if (DBManager.getManagerInstance().recordExists(DBManager.userSectionTable, "id_user_section", String.valueOf(idUS))) {
                // -1 error
                result = DBManager.getManagerInstance().deleteWithQuery(DBManager.userSectionTable, "id_user_section = ?", String.valueOf(idUS));
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

        if (user != null) {
            Cursor cS = DBManager.getManagerInstance().executeQuery("select s.id_section as id_section, s.section_name as section_name, s.abbrev as abbrev from section as s, user_section as us, user as u" +
                    " where s.id_section = us.id_section and us.id_user = u.id_user and u.id_user = " + user.getIdUser() + ";");

            while (cS.moveToNext()) {
                Section row = new Section();
                row.initWithCursor(cS);

                user.getArrSections().add(row);
            }

            cS.close();
        }

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
