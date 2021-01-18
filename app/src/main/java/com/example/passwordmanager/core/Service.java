package com.example.passwordmanager.core;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Service implements Serializable {



    private int code;
    private String name;
    private String username;
    private String password;
    private String note;
    private String expirationDate;

    public Service(int code, String name, String username, String password, String note, String expirationDate){
        this.code = code;
        this.name = name;
        this.username = username;
        this.password = password;
        this.note = note;
        this.expirationDate = expirationDate;
    }

    public Service(int code, String name, String username, String password, String note){
        this.code = code;
        this.name = name;
        this.username = username;
        this.password = password;
        this.note = note;
    }

    public boolean isExpired() {

        if(!expirationDate.isEmpty()){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date expiration = null;
            try {
                expiration = format.parse(expirationDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return expiration.compareTo(new Date()) < 0;
        }
        return false;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
