package com.pentera.passwordcracker.master.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class HashReaderService {

    public List<String> processFileWithMd5Hashes(MultipartFile file) throws IOException {
        List<String> hashes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                hashes.add(trimmedLine);
            }
        }
        return hashes;
    }
}

