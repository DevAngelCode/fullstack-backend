package com.fullstack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack_backend.payload.request.ProfileRequest;
import com.fullstack_backend.payload.response.UserProfileResponse;
import com.fullstack_backend.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENTE', 'ROLE_TECNICO')")
    public ResponseEntity<UserProfileResponse> getUserProfileByUsername(@PathVariable String username) {
        UserProfileResponse userProfile = usuarioService.getUserProfileByUsername(username);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENTE', 'ROLE_TECNICO')")
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable Long id) {
        UserProfileResponse userProfile = usuarioService.getUserProfileById(id);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENTE', 'ROLE_TECNICO')")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@PathVariable Long id, @RequestBody ProfileRequest profileRequest) {
        UserProfileResponse updatedProfile = usuarioService.updateUserProfile(id, profileRequest);
        return ResponseEntity.ok(updatedProfile);
    }
}
