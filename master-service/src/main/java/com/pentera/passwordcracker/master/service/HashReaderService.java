package com.pentera.passwordcracker.master.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HashReaderService {
    public List<String> readHashesFromFile(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename));
    }
}

