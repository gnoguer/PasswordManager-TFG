package com.example.passwordmanager;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase();
    private static final String DIGIT = "0123456789";
    private static final String OTHER_PUNCTUATION = "!@#&()–[{}]:;',?/*";
    private static final String OTHER_SYMBOL = "~$^+=";
    private static final String CHAR_SPECIAL = OTHER_PUNCTUATION + OTHER_SYMBOL;

    private static final String PASSWORD_ALLOW =
            CHAR_LOWERCASE + CHAR_UPPERCASE + DIGIT + CHAR_SPECIAL;

    private static final SecureRandom random = new SecureRandom();

    public String generateStrongPassword(int passwordLength) {

        StringBuilder result = new StringBuilder(passwordLength);

        // at least 2 lowercase
        String strLowerCase = generateRandomString(CHAR_LOWERCASE, 2);
        result.append(strLowerCase);

        // at least 2 uppercase
        String strUppercaseCase = generateRandomString(CHAR_UPPERCASE, 2);
        result.append(strUppercaseCase);

        // at least 2 digits
        String strDigit = generateRandomString(DIGIT, 2);
        result.append(strDigit);

        // at least 1 special characters
        String strSpecialChar = generateRandomString(CHAR_SPECIAL, 1);
        result.append(strSpecialChar);

        // rest are random
        String strOther = generateRandomString(PASSWORD_ALLOW, passwordLength - 7);
        result.append(strOther);

        return result.toString();
    }

    private String generateRandomString(String input, int size) {

        if (input == null || input.length() <= 0)
            throw new IllegalArgumentException("Invalid input.");
        if (size < 1) throw new IllegalArgumentException("Invalid size.");

        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            // produce a random order
            int index = random.nextInt(input.length());
            result.append(input.charAt(index));
        }
        return result.toString();
    }
}
