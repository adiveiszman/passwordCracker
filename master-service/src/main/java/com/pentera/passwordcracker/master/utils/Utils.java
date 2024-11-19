package com.pentera.passwordcracker.master.utils;

public class Utils {
    public static String formatPassword(long number) {
        String numberStr = String.format("%08d", number);
        return String.format("05%s-%s", numberStr.charAt(0), numberStr.substring(1));
    }
}

