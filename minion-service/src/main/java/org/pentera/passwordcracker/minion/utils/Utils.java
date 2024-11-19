package org.pentera.passwordcracker.minion.utils;

import java.math.BigInteger;

public class Utils {
    public static String formatPassword(long number) {
        String numberStr = '0' + String.valueOf(number);

        return numberStr.substring(0, 3) + "-" + numberStr.substring(3);
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
