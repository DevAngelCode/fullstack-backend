package com.fullstack_backend.controller;

import com.fullstack_backend.enums.ERol;
import com.fullstack_backend.model.Usuario;
import com.fullstack_backend.payload.response.HorarioDisponibleResponse;
import com.fullstack_backend.payload.response.SedeResponse;
import com.fullstack_backend.payload.response.ServicioResponse;
import com.fullstack_backend.payload.response.TecnicoDisponibleResponse;
import com.fullstack_backend.repository.UsuarioRepository;
import com.fullstack_backend.service.DisponibilidadService;
import com.fullstack_backend.service.SedeService;
import com.fullstack_backend.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private SedeService sedeService;

    @Autowired
    private DisponibilidadService disponibilidadService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioResponse>> getAllServicios() {
        List<ServicioResponse> servicios = servicioService.getAllServicios();
        return new ResponseEntity<>(servicios, HttpStatus.OK);
    }

    @GetMapping("/sedes")
    public ResponseEntity<List<SedeResponse>> getAllSedes() {
        List<SedeResponse> sedes = sedeService.getAllSedes();
        return new ResponseEntity<>(sedes, HttpStatus.OK);
    }

    @GetMapping("/tecnicos")
    public ResponseEntity<List<TecnicoDisponibleResponse>> getTecnicosDisponibles(
            @RequestParam Long sedeId,
            @RequestParam Long servicioId) {

        // Get all technicians
        List<Usuario> tecnicos = usuarioRepository.findByRoles_Nombre(ERol.ROLE_TECNICO);

        // Filter by sede and servicio
        List<TecnicoDisponibleResponse> tecnicosDisponibles = tecnicos.stream()
                .filter(t -> t.getEnabled())
                .filter(t -> t.getSede() != null && t.getSede().getId().equals(sedeId))
                .filter(t -> t.getServicios().stream().anyMatch(s -> s.getId().equals(servicioId)))
                .map(t -> {
                    TecnicoDisponibleResponse response = new TecnicoDisponibleResponse();
                    response.setId(t.getId());
                    response.setNombre(t.getNombre());
                    response.setApellido(t.getApellido());
                    response.setNombreCompleto(t.getNombre() + " " + t.getApellido());
                    return response;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(tecnicosDisponibles, HttpStatus.OK);
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<HorarioDisponibleResponse>> getHorariosDisponibles(
            @RequestParam Long sedeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<HorarioDisponibleResponse> horarios = disponibilidadService.getHorariosDisponibles(sedeId, fecha);
        return new ResponseEntity<>(horarios, HttpStatus.OK);
    }
}
