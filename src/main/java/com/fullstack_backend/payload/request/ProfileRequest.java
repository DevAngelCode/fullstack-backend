package com.fullstack_backend.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String nombre;
    private String apellido; // New field
    private String username; // New field
    private String email;
    private String telefono;
    private String imagenBase64;
    private String tipoImagen;
}
