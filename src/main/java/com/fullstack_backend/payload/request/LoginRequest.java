package com.fullstack_backend.payload.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String usernameOrEmail;
    private String password;
}
