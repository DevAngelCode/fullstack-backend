package com.fullstack_backend.service.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fullstack_backend.model.Usuario;
import com.fullstack_backend.payload.response.UserProfileResponse;
import com.fullstack_backend.repository.UsuarioRepository;
import com.fullstack_backend.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserProfileResponse getUserProfileByUsername(String username) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);

        if (usuarioOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username);
        }

        Usuario usuario = usuarioOptional.get();
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setId(usuario.getId());
        userProfileResponse.setNombre(usuario.getNombre());
        userProfileResponse.setUsername(usuario.getUsername());
        userProfileResponse.setEmail(usuario.getEmail());
        userProfileResponse.setRoles(usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toList()));
        userProfileResponse.setEnabled(usuario.getEnabled());

        return userProfileResponse;
    }

}
