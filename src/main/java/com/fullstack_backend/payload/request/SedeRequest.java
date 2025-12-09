package com.fullstack_backend.payload.request;

import lombok.Data;

@Data
public class SedeRequest {
    private String nombre;
    private String direccion;
    private String imagenBase64;
    private String tipoImagen;
}
