package com.example.passwordmanager.core;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase();
    private static final String DIGIT = "0123456789";
    private static final String OTHER_PUNCTUATION = "!@#&()–[{}]:;',?/*";
    private static final String OTHER_SYMBOL = "~$^+=";
    private static final String CHAR_SPECIAL = OTHER_PUNCTUATION + OTHER_SYMBOL;

    private static final int UPPER_CASE_LENGTH = 2;
    private static final int LOWER_CASE_LENGTH = 2;
    private static final int DIGIT_LENGTH = 2;
    private static final int SPECIAL_LENGTH = 1;





    private static final SecureRandom random = new SecureRandom();

    public String generateStrongPassword(int passwordLength, boolean upperCase, boolean lowerCase, boolean digits, boolean special ) {

       int length = 0;
       String passwordAllow = "";

        StringBuilder result = new StringBuilder(passwordLength);

        if(upperCase){
            //uppercase
            String strUppercaseCase = generateRandomString(CHAR_UPPERCASE, UPPER_CASE_LENGTH);
            result.append(strUppercaseCase);
            passwordAllow = passwordAllow + CHAR_UPPERCASE;
            length += UPPER_CASE_LENGTH;
        }

        if(lowerCase) {
            //lowercase
            String strLowerCase = generateRandomString(CHAR_LOWERCASE, LOWER_CASE_LENGTH);
            result.append(strLowerCase);
            passwordAllow = passwordAllow + CHAR_LOWERCASE;
            length += LOWER_CASE_LENGTH;
        }

        if(digits) {
            //digits
            String strDigit = generateRandomString(DIGIT, DIGIT_LENGTH);
            result.append(strDigit);
            passwordAllow = passwordAllow + DIGIT;
            length += DIGIT_LENGTH;
        }

        if(special) {
            //special characters
            String strSpecialChar = generateRandomString(CHAR_SPECIAL, SPECIAL_LENGTH);
            result.append(strSpecialChar);
            passwordAllow = passwordAllow + CHAR_SPECIAL;
            length += SPECIAL_LENGTH;
        }

        // rest are random
        String strOther = generateRandomString(passwordAllow, passwordLength - length);
        result.append(strOther);

        return result.toString();
    }

    private String generateRandomString(String input, int size) {

        if (input == null || input.length() <= 0)
            return "";
        if (size < 1)
            return "";

        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            // produce a random order
            int index = random.nextInt(input.length());
            result.append(input.charAt(index));
        }
        return result.toString();
    }
}
