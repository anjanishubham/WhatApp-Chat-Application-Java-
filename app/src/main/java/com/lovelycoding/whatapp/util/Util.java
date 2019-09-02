package com.lovelycoding.whatapp.util;

public class Util {
    public static boolean verifyEmailId(String email)
    {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);

    }



}
