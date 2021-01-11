package com.example.passwordmanager.core;

import java.io.Serializable;

public class BankAccount implements Serializable {
    private int code;
    private String name;
    private String IBAN;
    private String PIN;

    public BankAccount(int code, String name, String IBAN, String PIN) {
        this.code = code;
        this.name = name;
        this.IBAN = IBAN;
        this.PIN = PIN;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }
}
