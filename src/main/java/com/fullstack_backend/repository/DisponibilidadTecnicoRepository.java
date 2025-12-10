package com.fullstack_backend.repository;

import com.fullstack_backend.model.DisponibilidadTecnico;
import com.fullstack_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DisponibilidadTecnicoRepository extends JpaRepository<DisponibilidadTecnico, Long> {

    List<DisponibilidadTecnico> findByTecnicoAndFechaOrderByHoraInicioAsc(Usuario tecnico, LocalDate fecha);

    List<DisponibilidadTecnico> findBySedeIdAndFechaAndDisponibleTrueOrderByHoraInicioAsc(Long sedeId, LocalDate fecha);

    @Query("SELECT d FROM DisponibilidadTecnico d WHERE d.sede.id = :sedeId " +
            "AND d.fecha = :fecha AND d.disponible = true " +
            "AND d.horaInicio <= :hora AND d.horaFin > :hora")
    List<DisponibilidadTecnico> findAvailableTechniciansBySedeAndDateTime(
            @Param("sedeId") Long sedeId,
            @Param("fecha") LocalDate fecha,
            @Param("hora") LocalTime hora);
}
