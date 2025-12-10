package com.fullstack_backend.payload.response;

import com.fullstack_backend.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaResponse {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long servicioId;
    private String servicioNombre;
    private Double servicioPrecio;
    private Long sedeId;
    private String sedeNombre;
    private String sedeDireccion;
    private Long tecnicoId;
    private String tecnicoNombre;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estado;
    private String notas;
    private LocalDateTime fechaCreacion;
}
