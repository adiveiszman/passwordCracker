package com.pentera.passwordcracker.master.controller;

import com.pentera.passwordcracker.master.service.HashReaderService;
import com.pentera.passwordcracker.master.service.MasterService;
import org.pentera.passwordcracker.dto.TaskResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/master")
public class MasterController {
    private final MasterService masterService;
    private final HashReaderService hashReaderService;

    @Autowired
    public MasterController(MasterService masterService, HashReaderService hashReaderService) {
        this.masterService = masterService;
        this.hashReaderService = hashReaderService;
    }

    @PostMapping("/task/initiate")
    public ResponseEntity<String> uploadFileAndSetMinions(
            @RequestParam("file") MultipartFile file,
            @RequestParam("minionCount") int minionCount) {
        try {
            if (!file.isEmpty() && minionCount > 0) {
                List<String> hashes = hashReaderService.processFileWithMd5Hashes(file);
                masterService.setMinionCount(minionCount);
                masterService.distributeInitialTasks(hashes);
                return ResponseEntity.ok("Uploaded successfully, and tasks are being distributed.");
            } else {
                return ResponseEntity.badRequest().body("Invalid file or minion count.");
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing the file: " + e.getMessage());
        }
    }

    @PostMapping("/task/completion")
    public ResponseEntity<?> handleTaskCompletion(@RequestBody TaskResultDTO taskResult) {
        return ResponseEntity.ok(masterService.handleResult(taskResult));
    }

/*    @PostMapping("/update-minions")
    public ResponseEntity<String> updateMinionCount(@RequestParam("minionCount") int minionCount) {
        try {
            if (minionCount < 1) {
                return ResponseEntity.badRequest().body("Minion count must be at least 1.");
            }
            taskDistributor.adjustMinionCount(minionCount);
            return ResponseEntity.ok("Minion count updated to " + minionCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update minion count: " + e.getMessage());
        }
    }*/
}
