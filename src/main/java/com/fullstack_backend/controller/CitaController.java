package com.fullstack_backend.controller;

import com.fullstack_backend.payload.request.CitaRequest;
import com.fullstack_backend.payload.response.CitaResponse;
import com.fullstack_backend.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @PostMapping
    public ResponseEntity<CitaResponse> crearCita(
            @Valid @RequestBody CitaRequest citaRequest,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CitaResponse cita = citaService.crearCita(citaRequest, username);
            return new ResponseEntity<>(cita, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/mis-citas")
    public ResponseEntity<List<CitaResponse>> getMisCitas(Authentication authentication) {
        String username = authentication.getName();
        List<CitaResponse> citas = citaService.getMisCitas(username);
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> getCitaById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CitaResponse cita = citaService.getCitaById(id, username);
            return new ResponseEntity<>(cita, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponse> cancelarCita(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CitaResponse cita = citaService.cancelarCita(id, username);
            return new ResponseEntity<>(cita, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
