package org.pentera.passwordcracker.minion.controller;

import org.pentera.passwordcracker.dto.CrackRequestDTO;
import org.pentera.passwordcracker.dto.TaskResultDTO;
import org.pentera.passwordcracker.minion.service.MinionService;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/minion")
public class MinionController {
    private final MinionService crackPasswordService;

    public MinionController(MinionService crackPasswordService) {
        this.crackPasswordService = crackPasswordService;
    }

    @PostMapping("/crack")
    public ResponseEntity<String> crackHash(@RequestBody CrackRequestDTO request) {
        //NOTE: Controller is tightly coupled with the DTO,
        // which enhances the maintainability of the code and makes it easier to handle changes in the data structure
        // without impacting the service logic directly
        TaskResultDTO result = crackPasswordService.processRange(request.getHash(), request.getStartRange(), request.getEndRange());
        String password = result.getCrackedPassword();
        if (password != null) {
            return ResponseEntity.ok(password);
        }
        return ResponseEntity.notFound().build();
    }
}
