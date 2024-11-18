package org.pentera.passwordcracker.minion.controller;

import org.pentera.passwordcracker.dto.CrackRequestDTO;
import org.pentera.passwordcracker.minion.service.CrackPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crack")
public class MinionController {

    private final CrackPasswordService crackPasswordService;

    //NOTE: instead of @Autowired, it makes it easy to mock MinionController when testing
    public MinionController(CrackPasswordService crackPasswordService) {
        this.crackPasswordService = crackPasswordService;
    }

    @PostMapping
    public ResponseEntity<String> crackHash(@RequestBody CrackRequestDTO request) {
        //NOTE: Controller is tightly coupled with the DTO,
        // which enhances the maintainability of the code and makes it easier to handle changes in the data structure
        // without impacting the service logic directly
        String password = crackPasswordService.crackPassword(request.getHash(), request.getStartRange(), request.getEndRange());
        if (password != null) {
            return ResponseEntity.ok(password);
        }
        return ResponseEntity.notFound().build();
    }
}
