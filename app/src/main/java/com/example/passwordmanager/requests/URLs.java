package com.example.passwordmanager.requests;

public class URLs {

    private static final String ROOT_URL = "http://80.30.204.106:8080/android/Api.php?apicall=";

    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_GET_LEAKS = ROOT_URL + "getLeaks";

    public static final String URL_SAVE_PASSWORD = ROOT_URL + "savePass";
    public static final String URL_GET_SERVICES = ROOT_URL + "getServices";
    public static final String URL_GET_PASSWORDS = ROOT_URL + "getPasswords";
    public static final String URL_DELETE_SERVICE = ROOT_URL + "deleteService";
    public static final String URL_UPDATE_SERVICE = ROOT_URL + "updateService";

    public static final String URL_SAVE_NOTE = ROOT_URL + "saveNote";
    public static final String URL_GET_NOTES = ROOT_URL + "getNotes";
    public static final String URL_DELETE_NOTE = ROOT_URL + "deleteNote";
    public static final String URL_UPDATE_NOTE = ROOT_URL + "updateNote";

    public static final String URL_SAVE_PAYMENT_CARD = ROOT_URL + "savePaymentCard";
    public static final String URL_GET_PAYMENT_CARDS = ROOT_URL + "getPaymentCards";
    public static final String URL_DELETE_PAYMENT_CARD = ROOT_URL + "deletePaymentCard";
    public static final String URL_UPDATE_PAYMENT_CARD = ROOT_URL + "updatePaymentCard";

    public static final String URL_SAVE_BANK_ACC = ROOT_URL + "saveBankAcc";
    public static final String URL_GET_BANK_ACCS = ROOT_URL + "getBankAccs";
    public static final String URL_DELETE_BANK_ACC = ROOT_URL + "deleteBankAcc";
    public static final String URL_UPDATE_BANK_ACC = ROOT_URL + "updateBankAcc";
}
