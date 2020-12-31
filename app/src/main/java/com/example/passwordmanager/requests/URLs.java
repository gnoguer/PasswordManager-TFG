package com.example.passwordmanager.requests;

public class URLs {

    private static final String ROOT_URL = "http://10.0.2.2/android/Api.php?apicall=";

    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_SAVE_PASSWORD = ROOT_URL + "savePass";
    public static final String URL_GET_SERVICES = ROOT_URL + "getServices";
    public static final String URL_GET_LEAKS = ROOT_URL + "getLeaks";
    public static final String URL_GET_PASSWORDS = ROOT_URL + "getPasswords";
    public static final String URL_DELETE_SERVICE = ROOT_URL + "deleteService";

}
