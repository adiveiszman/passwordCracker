package org.pentera.passwordcracker.minion.service;

import org.pentera.passwordcracker.dto.CrackResultDTO;
import org.pentera.passwordcracker.minion.utils.Utils;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PasswordCrackMinionService {
    private final static String ALGORITHM = "MD5";

    public PasswordCrackMinionService() {}

    public CrackResultDTO processTask(String hash, long startRange, long endRange) {
        CrackResultDTO result = new CrackResultDTO();

        result.setHash(hash);
        try {
            String crackedPassword = crackPassword(hash, startRange, endRange);
            if (crackedPassword != null) {
                result.setCrackedPassword(crackedPassword);
                result.setStatus(CrackResultDTO.Status.CRACKED);
                System.out.println("Cracked: " + hash + " = " + crackedPassword + " in range: " + startRange + " - " + endRange);
            } else {
                result.setStatus(CrackResultDTO.Status.NOT_IN_RANGE);
                System.out.println("Could not crack hash: " + hash + " in range: " + startRange + " to " + endRange);
            }
        } catch (Exception e) {
            result.setStatus(CrackResultDTO.Status.FAILED);
            System.out.println("Failed to crack with error: " + e);
        }

        return result;
    }

    public String crackPassword(String hash, long startRange, long endRange) {
        try {
            MessageDigest md5Instance = MessageDigest.getInstance(ALGORITHM);
            for (long i = startRange; i <= endRange; i++) {
                String potentialPassword = Utils.formatPassword(i);
                String calculatedHash = Utils.toHex(md5Instance.digest(potentialPassword.getBytes()));
                if (calculatedHash.equals(hash)) {
                    return potentialPassword;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.fillInStackTrace();
        }

        return null;
    }
}
