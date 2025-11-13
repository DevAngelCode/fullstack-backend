package com.fullstack_backend.payload.response;

import java.util.List;

import lombok.Data;

@Data
public class RegisterResponse {
    private String message;
    private String nombre;
    private String username;
    private String email;
    private List<String> roles;

    public RegisterResponse(String message, String nombre, String username, String email, List<String> roles) {
        this.message = message;
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
