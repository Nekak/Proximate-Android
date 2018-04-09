package com.neklien.proximatetestandroid.helpers.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nekak on 08/04/18.
 */

public class Section implements DBElement {
    @SerializedName("id")
    @Expose
    private long idSection;

    @SerializedName("seccion")
    @Expose
    private String sectionName;

    @SerializedName("abrev")
    @Expose
    private String abbr;

    @Override
    public void initWithCursor(Cursor c) {
        idSection = c.getLong(c.getColumnIndex("id_section"));
        sectionName = c.getString(c.getColumnIndex("section_name"));
        abbr = c.getString(c.getColumnIndex("abbrev"));
    }

    @Override
    public ContentValues getContentValue() {
        ContentValues cv = new ContentValues();

        cv.put("id_section", this.idSection);
        cv.put("section_name", this.sectionName);
        cv.put("abbrev", this.abbr);

        return cv;
    }

    public long getIdSection() {
        return idSection;
    }

    public void setIdSection(long idSection) {
        this.idSection = idSection;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }
}
