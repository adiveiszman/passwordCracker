package org.pentera.passwordcracker.minion.utils;

import java.math.BigInteger;

public class Utils {
    public static String formatPassword(long number) {
        String numberStr = String.format("%08d", number);
        return String.format("05%s-%s", numberStr.charAt(0), numberStr.substring(1));
    }

    public static long passwordToLong(String password) {
        return Long.parseLong(password.replaceAll("-", ""));
    }

    public static String toHex(byte[] bytes) {
        BigInteger number = new BigInteger(1, bytes);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}
