package org.pentera.passwordcracker.minion.controller;

import org.pentera.passwordcracker.dto.CrackRequestDTO;
import org.pentera.passwordcracker.dto.CrackResultDTO;
import org.pentera.passwordcracker.minion.service.PasswordCrackMinionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/minion")
public class MinionController {
    private final PasswordCrackMinionService crackPasswordService;

    public MinionController(PasswordCrackMinionService crackPasswordService) {
        this.crackPasswordService = crackPasswordService;
    }

    @PostMapping("/crack")
    public ResponseEntity<CrackResultDTO> crackHash(@RequestBody CrackRequestDTO request) {
        CrackResultDTO result = crackPasswordService.processTask(request.getHash(), request.getStartRange(),
                request.getEndRange());
        if(result.getStatus() == CrackResultDTO.Status.CRACKED
                || result.getStatus() == CrackResultDTO.Status.NOT_IN_RANGE) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
