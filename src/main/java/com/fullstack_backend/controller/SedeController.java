package com.fullstack_backend.controller;

import com.fullstack_backend.payload.request.SedeRequest;
import com.fullstack_backend.payload.response.SedeResponse;
import com.fullstack_backend.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sedes")
public class SedeController {

    @Autowired
    private SedeService sedeService;

    @GetMapping
    public ResponseEntity<List<SedeResponse>> getAllSedes() {
        return ResponseEntity.ok(sedeService.getAllSedes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SedeResponse> getSede(@PathVariable Long id) {
        return ResponseEntity.ok(sedeService.getSedeById(id));
    }

    @PostMapping
    public ResponseEntity<SedeResponse> createSede(@RequestBody SedeRequest sedeRequest) {
        return ResponseEntity.ok(sedeService.createSede(sedeRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SedeResponse> updateSede(@PathVariable Long id, @RequestBody SedeRequest sedeRequest) {
        return ResponseEntity.ok(sedeService.updateSede(id, sedeRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSede(@PathVariable Long id) {
        sedeService.deleteSede(id);
        return ResponseEntity.ok().build();
    }
}
