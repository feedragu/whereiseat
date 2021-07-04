package com.ancora.gmaps2.federico.googlemapsactivity.models;

/**
 * Created by Federico on 22/04/2016.
 */
public class AccountReg {
    private int codice;

    public AccountReg(int codice, String email, String username, String password, String photoPath, boolean log) {
        this.codice = codice;
        this.email = email;
        this.username = username;
        this.password = password;
        this.photoPath = photoPath;
        this.log = log;
    }

    private String email;

    public int getCodice() {
        return codice;
    }

    public void setCodice(int codice) {
        this.codice = codice;
    }

    private String username, password, photoPath;
    private boolean log;


    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
}
