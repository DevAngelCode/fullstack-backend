package com.fullstack_backend.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserManagementRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private String password; // Optional for updates, validated in service layer

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    private String telefono;

    private Set<String> roles; // Role names: "ROLE_ADMIN", "ROLE_CLIENTE", "ROLE_TECNICO"

    // For technicians only
    private Long sedeId;
    private Set<Long> servicioIds;

    private Boolean enabled = true;
}
