package com.example.passwordmanager.core;

import java.io.Serializable;

public class Note implements Serializable {

    private int code;
    private String name;
    private String note;

    public Note(int noteCode, String noteName, String note) {
        this.code = noteCode;
        this.name = noteName;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
