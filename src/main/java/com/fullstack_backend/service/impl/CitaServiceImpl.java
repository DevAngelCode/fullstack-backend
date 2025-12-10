package com.fullstack_backend.service.impl;

import com.fullstack_backend.enums.EstadoCita;
import com.fullstack_backend.model.*;
import com.fullstack_backend.payload.request.CitaRequest;
import com.fullstack_backend.payload.response.CitaResponse;
import com.fullstack_backend.repository.*;
import com.fullstack_backend.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @Override
    @Transactional
    public CitaResponse crearCita(CitaRequest citaRequest, String username) {
        // Find user
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Find service
        Servicio servicio = servicioRepository.findById(citaRequest.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        // Find sede
        Sede sede = sedeRepository.findById(citaRequest.getSedeId())
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        // Find technician
        Usuario tecnico = usuarioRepository.findById(citaRequest.getTecnicoId())
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));

        // Check if technician already has appointment at this time
        boolean tecnicoOcupado = citaRepository.existsByTecnicoAndFechaAndHoraAndEstadoNot(
                tecnico, citaRequest.getFecha(), citaRequest.getHora(), EstadoCita.CANCELADA);

        if (tecnicoOcupado) {
            throw new RuntimeException("El técnico ya tiene una cita en este horario");
        }

        // Create appointment
        Cita cita = new Cita();
        cita.setUsuario(usuario);
        cita.setServicio(servicio);
        cita.setSede(sede);
        cita.setTecnico(tecnico);
        cita.setFecha(citaRequest.getFecha());
        cita.setHora(citaRequest.getHora());
        cita.setNotas(citaRequest.getNotas());
        cita.setEstado(EstadoCita.PENDIENTE);

        Cita savedCita = citaRepository.save(cita);

        return mapToResponse(savedCita);
    }

    @Override
    public List<CitaResponse> getMisCitas(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return citaRepository.findByUsuarioOrderByFechaDescHoraDesc(usuario).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CitaResponse getCitaById(Long id, String username) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // Verify user owns this appointment
        if (!cita.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para ver esta cita");
        }

        return mapToResponse(cita);
    }

    @Override
    @Transactional
    public CitaResponse cancelarCita(Long id, String username) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // Verify user owns this appointment
        if (!cita.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para cancelar esta cita");
        }

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya está cancelada");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        Cita updatedCita = citaRepository.save(cita);

        return mapToResponse(updatedCita);
    }

    @Override
    public List<CitaResponse> getAllCitas() {
        return citaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CitaResponse mapToResponse(Cita cita) {
        CitaResponse response = new CitaResponse();
        response.setId(cita.getId());
        response.setUsuarioId(cita.getUsuario().getId());
        response.setUsuarioNombre(cita.getUsuario().getNombre() + " " + cita.getUsuario().getApellido());
        response.setServicioId(cita.getServicio().getId());
        response.setServicioNombre(cita.getServicio().getNombre());
        response.setServicioPrecio(cita.getServicio().getPrecio());
        response.setSedeId(cita.getSede().getId());
        response.setSedeNombre(cita.getSede().getNombre());
        response.setSedeDireccion(cita.getSede().getDireccion());
        response.setTecnicoId(cita.getTecnico().getId());
        response.setTecnicoNombre(cita.getTecnico().getNombre() + " " + cita.getTecnico().getApellido());
        response.setFecha(cita.getFecha());
        response.setHora(cita.getHora());
        response.setEstado(cita.getEstado());
        response.setNotas(cita.getNotas());
        response.setFechaCreacion(cita.getFechaCreacion());
        return response;
    }
}
