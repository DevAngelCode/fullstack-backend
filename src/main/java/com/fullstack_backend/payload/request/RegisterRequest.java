package com.fullstack_backend.payload.request;

import java.util.List;

import lombok.Data;

@Data
public class RegisterRequest {

    private String nombre;
    private String username;

    private String email;

    private List<String> roles; // Changed from Set to List

    private String password;
}

