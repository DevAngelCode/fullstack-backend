package com.fullstack_backend.service;

import com.fullstack_backend.payload.request.ProfileRequest;
import com.fullstack_backend.payload.response.UserProfileResponse;

public interface UsuarioService {
    UserProfileResponse getUserProfileByUsername(String username);
    UserProfileResponse getUserProfileById(Long id);
    UserProfileResponse updateUserProfile(Long id, ProfileRequest profileRequest);
}
