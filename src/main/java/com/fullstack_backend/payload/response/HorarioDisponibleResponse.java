package com.fullstack_backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDisponibleResponse {

    private Long tecnicoId;
    private String tecnicoNombre;
    private String tecnicoApellido;
    private LocalDate fecha;
    private LocalTime hora;
    private Boolean disponible;
}
