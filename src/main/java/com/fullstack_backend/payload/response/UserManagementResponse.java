package com.fullstack_backend.payload.response;

import lombok.Data;

import java.util.Set;

@Data
public class UserManagementResponse {
    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private Set<String> roles;
    private Boolean enabled;

    // For technicians
    private Long sedeId;
    private String sedeNombre;
    private Set<Long> servicioIds;
    private Set<String> servicioNombres;
}
