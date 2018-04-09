package com.neklien.proximatetestandroid.helpers.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by nekak on 08/04/18.
 */

public class UserSection implements DBElement {
    private long idUserSection;
    private long idUser;
    private long idSection;

    public UserSection() {
        idUserSection = 0;
        idUser = 0;
        idSection = 0;
    }

    @Override
    public void initWithCursor(Cursor c) {
        idUserSection = c.getLong(c.getColumnIndex("id_user_section"));
        idUser = c.getLong(c.getColumnIndex("id_user"));
        idSection = c.getLong(c.getColumnIndex("id_section"));
    }

    @Override
    public ContentValues getContentValue() {
        ContentValues cv = new ContentValues();

        cv.put("id_user_section", this.idUserSection);
        cv.put("id_user", this.idUser);
        cv.put("id_section", this.idSection);

        return cv;
    }

    public long getIdUserSection() {
        return idUserSection;
    }

    public void setIdUserSection(long idUserSection) {
        this.idUserSection = idUserSection;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public long getIdSection() {
        return idSection;
    }

    public void setIdSection(long idSection) {
        this.idSection = idSection;
    }
}
