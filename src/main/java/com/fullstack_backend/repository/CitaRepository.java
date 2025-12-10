package com.fullstack_backend.repository;

import com.fullstack_backend.model.Cita;
import com.fullstack_backend.model.Usuario;
import com.fullstack_backend.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByUsuarioOrderByFechaDescHoraDesc(Usuario usuario);

    List<Cita> findByTecnicoAndFechaOrderByHoraAsc(Usuario tecnico, LocalDate fecha);

    List<Cita> findBySedeIdAndFechaOrderByHoraAsc(Long sedeId, LocalDate fecha);

    boolean existsByTecnicoAndFechaAndHoraAndEstadoNot(Usuario tecnico, LocalDate fecha, LocalTime hora,
            EstadoCita estado);

    List<Cita> findByEstado(EstadoCita estado);
}
