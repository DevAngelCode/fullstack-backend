package com.fullstack_backend.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaRequest {

    @NotNull(message = "El servicio es requerido")
    private Long servicioId;

    @NotNull(message = "La sede es requerida")
    private Long sedeId;

    @NotNull(message = "El técnico es requerido")
    private Long tecnicoId;

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha;

    @NotNull(message = "La hora es requerida")
    private LocalTime hora;

    private String notas;
}
