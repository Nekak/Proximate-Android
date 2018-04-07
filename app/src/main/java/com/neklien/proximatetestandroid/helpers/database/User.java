package com.neklien.proximatetestandroid.helpers.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by nekak on 07/04/18.
 */

public class User implements DBElement {
    private long idUser;
    private String name;
    private int age;
    private String job;
    private String introduction;
    private String pathPicture;
    private float latitude;
    private float longitude;

    public User() {
        idUser = 0;
        name = "";
        age = 0;
        job = "";
        introduction = "";
        pathPicture = "";
        latitude = 0;
        longitude = 0;
    }

    @Override
    public void initWithCursor(Cursor c) {
        idUser = c.getLong(c.getColumnIndex("userId"));
        name = c.getString(c.getColumnIndex("name"));
        age = c.getInt(c.getColumnIndex("age"));
        job = c.getString(c.getColumnIndex("job"));
        introduction = c.getString(c.getColumnIndex("introduction"));
        pathPicture = c.getString(c.getColumnIndex("pathPicture"));
        latitude = c.getFloat(c.getColumnIndex("latitude"));
        longitude = c.getFloat(c.getColumnIndex("longitude"));
    }

    @Override
    public ContentValues getContentValue() {
        ContentValues cv=new ContentValues();

        cv.put("name",this.name);
        cv.put("age",this.age);
        cv.put("job",this.job);
        cv.put("introduction",this.introduction);
        cv.put("pathPicture",this.pathPicture);
        cv.put("latitude",this.latitude);
        cv.put("longitude",this.longitude);

        return cv;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPathPicture() {
        return pathPicture;
    }

    public void setPathPicture(String pathPicture) {
        this.pathPicture = pathPicture;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
