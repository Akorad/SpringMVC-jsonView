package org.Akorad.controller;

import lombok.RequiredArgsConstructor;
import org.Akorad.service.security.FailedLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final FailedLoginService failedLoginService;

    @PreAuthorize("hasRole('SUPER_ADMIN]')")
    @PostMapping("/unlock/{username}")
    public ResponseEntity<String> unlockUser(@PathVariable String username) {
        failedLoginService.unlockUser(username);
        return ResponseEntity.ok("User " + username + " has been unlocked.");
    }

}
