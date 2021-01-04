package com.example.passwordmanager.core;

import java.io.Serializable;
import java.util.Date;

public class PaymentCard implements Serializable {

    private int code;
    private String name;
    private String nameOnCard;
    private String number;
    private String securityCode;
    private String expirationDate;

    public PaymentCard(int code, String name, String nameOnCard, String number, String securityCode, String expirationDate) {
        this.code = code;
        this.name = name;
        this.nameOnCard = nameOnCard;
        this.number = number;
        this.securityCode = securityCode;
        this.expirationDate = expirationDate;
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

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
