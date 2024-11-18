package org.pentera.passwordcracker.minion.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

@Service
public class CrackPasswordService {
    private final static String ALGORITHM = "MD5";

    public String crackPassword(String hash, String startRange, String endRange) {
        long start = passwordToLong(startRange);
        long end = passwordToLong(endRange);

        try {
            MessageDigest md5Instance = MessageDigest.getInstance(ALGORITHM);
            for (long i = start; i <= end; i++) {
                String potentialPassword = formatPassword(i);
                String calculatedHash = toHex(md5Instance.digest(potentialPassword.getBytes()));
                if (calculatedHash.equals(hash)) {
                    return potentialPassword;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.fillInStackTrace();
        }

        return null;
    }

    private long passwordToLong (String password) {
        return Long.parseLong(password.replaceAll("-", ""));
    }

    private String formatPassword (long number) {
        String numberStr = '0' + String.valueOf(number);

        return numberStr.substring(0, 3) + "-" + numberStr.substring(3);
    }

    private String toHex(byte[] bytes) {
        BigInteger number = new BigInteger(1, bytes);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}
