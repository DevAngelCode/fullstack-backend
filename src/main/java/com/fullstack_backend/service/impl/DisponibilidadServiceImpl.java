package com.fullstack_backend.service.impl;

import com.fullstack_backend.enums.ERol;
import com.fullstack_backend.enums.EstadoCita;
import com.fullstack_backend.model.Usuario;
import com.fullstack_backend.payload.response.HorarioDisponibleResponse;
import com.fullstack_backend.repository.CitaRepository;
import com.fullstack_backend.repository.UsuarioRepository;
import com.fullstack_backend.service.DisponibilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisponibilidadServiceImpl implements DisponibilidadService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Override
    public List<HorarioDisponibleResponse> getHorariosDisponibles(Long sedeId, LocalDate fecha) {
        List<HorarioDisponibleResponse> horariosDisponibles = new ArrayList<>();

        // Get all technicians with ROLE_TECNICO
        List<Usuario> tecnicos = usuarioRepository.findByRoles_Nombre(ERol.ROLE_TECNICO);

        // Filter technicians by sede (only technicians assigned to this sede)
        tecnicos = tecnicos.stream()
                .filter(tecnico -> tecnico.getSede() != null && tecnico.getSede().getId().equals(sedeId))
                .filter(tecnico -> tecnico.getEnabled()) // Only enabled technicians
                .toList();

        // For each technician, generate hourly slots from 8 AM to 6 PM
        for (Usuario tecnico : tecnicos) {
            for (int hour = 8; hour < 18; hour++) {
                LocalTime hora = LocalTime.of(hour, 0);

                if (fecha.isEqual(LocalDate.now()) && hora.isBefore(LocalTime.now())) {
                    continue;
                }

                // Check if technician has an appointment at this time
                boolean ocupado = citaRepository.existsByTecnicoAndFechaAndHoraAndEstadoNot(
                        tecnico, fecha, hora, EstadoCita.CANCELADA);

                if (!ocupado) {
                    HorarioDisponibleResponse horario = new HorarioDisponibleResponse();
                    horario.setTecnicoId(tecnico.getId());
                    horario.setTecnicoNombre(tecnico.getNombre());
                    horario.setTecnicoApellido(tecnico.getApellido());
                    horario.setFecha(fecha);
                    horario.setHora(hora);
                    horario.setDisponible(true);
                    horariosDisponibles.add(horario);
                }
            }
        }

        return horariosDisponibles;
    }
}
