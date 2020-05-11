package com.example.mytrip.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isUserEmailValid(String email) {
        if (email == null) {
            return false;
        }
        final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public static boolean isNamedValid(String name) {
        return name != null && name.trim().length() > 2;
    }
}
