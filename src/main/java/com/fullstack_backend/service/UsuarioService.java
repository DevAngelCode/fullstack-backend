package com.fullstack_backend.service;

import com.fullstack_backend.payload.response.UserProfileResponse;

public interface UsuarioService {
    UserProfileResponse getUserProfileByUsername(String username);
}
