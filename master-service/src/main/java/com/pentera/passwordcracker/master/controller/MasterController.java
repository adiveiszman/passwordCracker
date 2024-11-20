package com.pentera.passwordcracker.master.controller;

import com.pentera.passwordcracker.master.service.HashReaderService;
import com.pentera.passwordcracker.master.service.PasswordCrackMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/master")
public class MasterController {
    private final PasswordCrackMasterService masterService;
    private final HashReaderService hashReaderService;

    @Autowired
    public MasterController(PasswordCrackMasterService masterService, HashReaderService hashReaderService) {
        this.masterService = masterService;
        this.hashReaderService = hashReaderService;
    }

    @PostMapping("/task/initiate")
    public ResponseEntity<String> uploadFileAndSetMinions(
            @RequestParam("file") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                List<String> hashes = hashReaderService.processFileWithMd5Hashes(file);
                masterService.distributeInitialTasks(hashes);
                return ResponseEntity.ok("File uploaded successfully, and tasks are being distributed.");
            } else {
                return ResponseEntity.badRequest().body("Invalid file.");
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing the file: " + e.getMessage());
        }
    }
}
