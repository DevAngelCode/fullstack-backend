package com.fullstack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack_backend.payload.request.LoginRequest;
import com.fullstack_backend.payload.request.RegisterRequest;
import com.fullstack_backend.payload.response.LoginResponse;
import com.fullstack_backend.payload.response.RegisterResponse;
import com.fullstack_backend.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        if (loginResponse.getMessage() != null && loginResponse.getMessage().startsWith("Error:")) {
            return new ResponseEntity<>(loginResponse, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authService.register(registerRequest);
        if (registerResponse.getMessage() != null && registerResponse.getMessage().startsWith("Error:")) {
            return new ResponseEntity<>(registerResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }
}

