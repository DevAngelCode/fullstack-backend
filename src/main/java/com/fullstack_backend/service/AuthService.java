package com.fullstack_backend.service;

import com.fullstack_backend.payload.request.LoginRequest;
import com.fullstack_backend.payload.request.RegisterRequest;
import com.fullstack_backend.payload.response.LoginResponse;
import com.fullstack_backend.payload.response.RegisterResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    RegisterResponse register(RegisterRequest registerRequest);
}
