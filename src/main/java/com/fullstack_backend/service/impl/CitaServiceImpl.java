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
import java.time.LocalTime;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

        // Check if technician already has appointment at this time (overlapping)
        // Assuming 1 hour duration for all services
        List<Cita> citasDelDia = citaRepository.findByTecnicoAndFechaAndEstadoNot(
                tecnico, citaRequest.getFecha(), EstadoCita.CANCELADA);

        boolean tecnicoOcupado = false;
        LocalTime newHora = citaRequest.getHora();
        LocalTime newFin = newHora.plusHours(1);

        for (Cita existingCita : citasDelDia) {
            LocalTime existingHora = existingCita.getHora();
            LocalTime existingFin = existingHora.plusHours(1);

            // Check overlap: start1 < end2 && start2 < end1
            if (newHora.isBefore(existingFin) && newHora.isAfter(existingHora) ||
                    existingHora.isBefore(newFin) && existingHora.isAfter(newHora) ||
                    newHora.equals(existingHora)) {
                tecnicoOcupado = true;
                break;
            }
        }

        if (tecnicoOcupado) {
            throw new RuntimeException(
                    "El técnico ya tiene una cita en conflicto con este horario (se requiere 1 hora de disponibilidad)");
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

        List<Cita> citas;
        boolean isTecnico = usuario.getRoles().stream()
                .anyMatch(r -> r.getNombre().name().equals("ROLE_TECNICO"));

        if (isTecnico) {
            citas = citaRepository.findByTecnicoOrderByFechaDescHoraDesc(usuario);
        } else {
            citas = citaRepository.findByUsuarioOrderByFechaDescHoraDesc(usuario);
        }

        return citas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CitaResponse getCitaById(Long id, String username) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // Verify user owns this appointment or is the assigned technician
        boolean isOwner = cita.getUsuario().getUsername().equals(username);
        boolean isAssignedTecnico = cita.getTecnico().getUsername().equals(username);

        if (!isOwner && !isAssignedTecnico) {
            // Check if admin? For now strict
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
    @Transactional
    public CitaResponse updateEstadoCita(Long id, String nuevoEstado, String username) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Only assigned technician or admin can update status
        boolean isAssignedTecnico = cita.getTecnico().getUsername().equals(username);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getNombre().name().equals("ROLE_ADMIN"));

        if (!isAssignedTecnico && !isAdmin) {
            throw new RuntimeException("No tienes permiso para actualizar esta cita");
        }

        EstadoCita estadoEnum;
        try {
            estadoEnum = EstadoCita.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + nuevoEstado);
        }

        // Logic Check: Can only Complete if Confirmed
        if (estadoEnum == EstadoCita.COMPLETADA && cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new RuntimeException("No se puede completar una cita que no ha sido confirmada previamente.");
        }

        cita.setEstado(estadoEnum);
        Cita updatedCita = citaRepository.save(cita);
        return mapToResponse(updatedCita);
    }

    @Override
    public List<CitaResponse> getAllCitas() {
        return citaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generarReciboPdf(Long id, String username) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isOwner = cita.getUsuario().getUsername().equals(username);
        boolean isAssignedTecnico = cita.getTecnico().getUsername().equals(username);
        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getNombre().name().equals("ROLE_ADMIN"));

        if (!isOwner && !isAssignedTecnico && !isAdmin) {
            throw new RuntimeException("No tienes permiso para ver este recibo");
        }

        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Comprobante de Cita");
            contentStream.endText();

            // Details
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(50, 700);

            contentStream.showText("ID Cita: " + cita.getId());
            contentStream.newLine();
            contentStream.showText("Cliente: " + cita.getUsuario().getNombre() + " " + cita.getUsuario().getApellido());
            contentStream.newLine();
            contentStream.showText("Servicio: " + cita.getServicio().getNombre());
            contentStream.newLine();
            contentStream.showText("Técnico: " + cita.getTecnico().getNombre() + " " + cita.getTecnico().getApellido());
            contentStream.newLine();
            contentStream.showText("Fecha: " + cita.getFecha());
            contentStream.newLine();
            contentStream.showText("Hora: " + cita.getHora());
            contentStream.newLine();
            contentStream.showText("Estado: " + cita.getEstado().name());
            contentStream.newLine();
            contentStream.showText("Sede: " + cita.getSede().getNombre());
            contentStream.newLine();
            contentStream.showText("Dirección: " + cita.getSede().getDireccion());
            contentStream.newLine();
            contentStream.newLine();

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Total: S/ " + cita.getServicio().getPrecio());

            contentStream.endText();
            contentStream.close();

            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el PDF del recibo", e);
        }
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
