package com.fullstack_backend.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String nombre;
    private String apellido; // New field
    private String username;
    private String email;
    private String telefono; // New field
    private List<String> roles;
    private Boolean enabled; // Include enabled status
    private String imagenBase64;
    private String tipoImagen;
}
