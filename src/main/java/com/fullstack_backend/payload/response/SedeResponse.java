package com.fullstack_backend.payload.response;

import lombok.Data;

@Data
public class SedeResponse {
    private Long id;
    private String nombre;
    private String direccion;
    private String imagenBase64;
    private String tipoImagen;
}
