package com.fullstack_backend.payload.response;

import lombok.Data;

@Data
public class TecnicoDisponibleResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
}
