package com.fullstack_backend.controller;

import com.fullstack_backend.payload.request.ServicioRequest;
import com.fullstack_backend.payload.response.ServicioResponse;
import com.fullstack_backend.service.IServicioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioController {

    @Autowired
    private IServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioResponse>> getAllServicios() {
        List<ServicioResponse> servicios = servicioService.getAllServicios();
        return new ResponseEntity<>(servicios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponse> getServicioById(@PathVariable Long id) {
        return servicioService.getServicioById(id)
                .map(servicio -> new ResponseEntity<>(servicio, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ServicioResponse> createServicio(@Valid @RequestBody ServicioRequest servicioRequest) {
        ServicioResponse createdServicio = servicioService.createServicio(servicioRequest);
        return new ResponseEntity<>(createdServicio, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponse> updateServicio(@PathVariable Long id, @Valid @RequestBody ServicioRequest servicioRequest) {
        try {
            ServicioResponse updatedServicio = servicioService.updateServicio(id, servicioRequest);
            return new ResponseEntity<>(updatedServicio, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteServicio(@PathVariable Long id) {
        try {
            servicioService.deleteServicio(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
