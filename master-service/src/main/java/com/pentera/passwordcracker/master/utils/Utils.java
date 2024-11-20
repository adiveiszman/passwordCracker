package com.pentera.passwordcracker.master.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Utils {
    public static String formatPassword(long number) {
        String numberStr = String.format("%08d", number);
        return String.format("05%s-%s", numberStr.charAt(0), numberStr.substring(1));
    }

    public static long passwordToLong(String password) {
        return Long.parseLong(password.replace("-", ""));
    }

    public static List<String> getMinionsEndpointsFromProps() {
        Properties props = new Properties();

        try (InputStream input = Files.newInputStream(Paths
                .get("master-service/src/main/resources/application.properties"))) {
            props.load(input);
            String endpoints = props.getProperty("minion.endpoints");
            return Arrays.asList(endpoints.split("\\s*,\\s*"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

