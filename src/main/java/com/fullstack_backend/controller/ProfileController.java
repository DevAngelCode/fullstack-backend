package com.fullstack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack_backend.payload.response.UserProfileResponse;
import com.fullstack_backend.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
        UserProfileResponse userProfile = usuarioService.getUserProfileByUsername(username);
        return ResponseEntity.ok(userProfile);
    }
}
