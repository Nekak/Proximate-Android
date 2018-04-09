package com.neklien.proximatetestandroid.helpers.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nekak on 07/04/18.
 */

public class User implements DBElement {
    @SerializedName("id")
    @Expose
    private long idUser;

    @SerializedName("nombres")
    @Expose
    private String names;

    @SerializedName("apellidos")
    @Expose
    private String lastnames;

    @SerializedName("correo")
    @Expose
    private String email;

    @SerializedName("numero_documento")
    @Expose
    private String docNumber;

    @SerializedName("ultima_sesion")
    @Expose
    private String lastLogin;

    @SerializedName("eliminado")
    @Expose
    private Integer deleted;

    @SerializedName("documentos_id")
    @Expose
    private long idDocuments;

    @SerializedName("documentos_abrev")
    @Expose
    private String abbr;

    @SerializedName("documentos_label")
    @Expose
    private String documentLabel;

    @SerializedName("estados_usuarios_label")
    @Expose
    private String statusUserLabel;

    @SerializedName("secciones")
    @Expose
    private ArrayList<Section> arrSections;

    private String pathPicture;
    private float latitude;
    private float longitude;

    public User() {
        idUser = 0;
        names = "";
        lastnames = "";
        email = "";
        docNumber = "";
        lastLogin = "";
        deleted = 0;
        idDocuments = 0;
        abbr = "";
        documentLabel = "";
        statusUserLabel = "";
        arrSections = new ArrayList<>();

        pathPicture = "";
        latitude = 0;
        longitude = 0;
    }

    @Override
    public void initWithCursor(Cursor c) {
        idUser = c.getLong(c.getColumnIndex("id_user"));
        ;
        names = c.getString(c.getColumnIndex("names"));
        lastnames = c.getString(c.getColumnIndex("lastnames"));
        email = c.getString(c.getColumnIndex("email"));
        docNumber = c.getString(c.getColumnIndex("document_number"));
        lastLogin = c.getString(c.getColumnIndex("lastLogin"));
        deleted = c.getInt(c.getColumnIndex("deleted"));
        idDocuments = c.getLong(c.getColumnIndex("idDocuments"));
        abbr = c.getString(c.getColumnIndex("documents_abb"));
        documentLabel = c.getString(c.getColumnIndex("documents_label"));
        statusUserLabel = c.getString(c.getColumnIndex("user_status_label"));

        pathPicture = c.getString(c.getColumnIndex("path_picture"));
        latitude = c.getFloat(c.getColumnIndex("latitude"));
        longitude = c.getFloat(c.getColumnIndex("longitude"));
    }

    @Override
    public ContentValues getContentValue() {
        ContentValues cv = new ContentValues();

        cv.put("id_user", this.idUser);
        cv.put("names", this.names);
        cv.put("lastnames", this.lastnames);
        cv.put("email", this.email);
        cv.put("document_number", this.docNumber);
        cv.put("lastLogin", this.lastLogin);
        cv.put("deleted", this.deleted);
        cv.put("idDocuments", this.idDocuments);
        cv.put("documents_abb", this.abbr);
        cv.put("documents_label", this.documentLabel);
        cv.put("user_status_label", this.statusUserLabel);

        cv.put("path_picture", this.pathPicture);
        cv.put("latitude", this.latitude);
        cv.put("longitude", this.longitude);

        return cv;
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

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getLastnames() {
        return lastnames;
    }

    public void setLastnames(String lastnames) {
        this.lastnames = lastnames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public long getIdDocuments() {
        return idDocuments;
    }

    public void setIdDocuments(long idDocuments) {
        this.idDocuments = idDocuments;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getDocumentLabel() {
        return documentLabel;
    }

    public void setDocumentLabel(String documentLabel) {
        this.documentLabel = documentLabel;
    }

    public String getStatusUserLabel() {
        return statusUserLabel;
    }

    public void setStatusUserLabel(String statusUserLabel) {
        this.statusUserLabel = statusUserLabel;
    }

    public ArrayList<Section> getArrSections() {
        return arrSections;
    }

    public void setArrSections(ArrayList<Section> arrSections) {
        this.arrSections = arrSections;
    }
}
