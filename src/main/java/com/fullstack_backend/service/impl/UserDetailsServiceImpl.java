package com.fullstack_backend.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fullstack_backend.model.Usuario;
import com.fullstack_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username o email
        Usuario usuario = usuarioRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuario no encontrado con username o email: " + username));
        return usuario;

    }

}
