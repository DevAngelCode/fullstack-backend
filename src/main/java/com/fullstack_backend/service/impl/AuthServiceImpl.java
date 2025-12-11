package com.fullstack_backend.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fullstack_backend.enums.ERol;
import com.fullstack_backend.model.Rol;
import com.fullstack_backend.model.Usuario;
import com.fullstack_backend.payload.request.LoginRequest;
import com.fullstack_backend.payload.request.RegisterRequest;
import com.fullstack_backend.payload.response.LoginResponse;
import com.fullstack_backend.payload.response.RegisterResponse;
import com.fullstack_backend.repository.RolRepository;
import com.fullstack_backend.repository.UsuarioRepository;
import com.fullstack_backend.security.jwt.JwtUtil;
import com.fullstack_backend.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    RolRepository rolRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);

            Usuario userDetails = (Usuario) authentication.getPrincipal(); // Cast to Usuario
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            String imagenBase64 = null;
            if (userDetails.getImagenData() != null && userDetails.getImagenData().length > 0) {
                imagenBase64 = "data:" + userDetails.getTipoImagen() + ";base64," +
                        java.util.Base64.getEncoder().encodeToString(userDetails.getImagenData());
            }

            return new LoginResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles,
                    "Login successful!",
                    imagenBase64,
                    userDetails.getTipoImagen()); // Pass success message
        } catch (AuthenticationException e) {
            // Return a LoginResponse with an error message and null for other fields
            return new LoginResponse(null, null, null, null, null, "Error: Credenciales incorrectas.", null, null);
        }
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            return new RegisterResponse("Error: ¡El nombre de usuario ya está en uso!", null, null, null, null);
        }

        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            return new RegisterResponse("Error: ¡El correo electrónico ya está en uso!", null, null, null, null);
        }

        // Create new user's account
        Usuario user = new Usuario();
        user.setNombre(registerRequest.getNombre());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword()));

        // Asignación de roles

        List<String> strRolesList = Optional.ofNullable(registerRequest.getRoles()).orElse(new ArrayList<>());

        Set<String> strRoles = new HashSet<>(strRolesList); // Convertir a Set para evitar duplicados
        Set<Rol> roles = new HashSet<>();// Set para almacenar los roles de tipo Rol

        if (strRoles.isEmpty()) {
            // Asigna ROLE_CLIENTE por defecto si no se especifican roles
            Rol clienteRol = rolRepository.findByNombre(ERol.ROLE_CLIENTE)
                    .orElseThrow(() -> new RuntimeException("Error: Rol de cliente no encontrado."));
            roles.add(clienteRol);

        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Rol adminRol = rolRepository.findByNombre(ERol.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Rol de administrador no encontrado."));
                        roles.add(adminRol);
                        break;
                    case "cliente":
                        Rol clienteRol = rolRepository.findByNombre(ERol.ROLE_CLIENTE)
                                .orElseThrow(() -> new RuntimeException("Error: Rol de cliente no encontrado."));
                        roles.add(clienteRol);
                        break;
                    case "tecnico":
                        Rol tecnicoRol = rolRepository.findByNombre(ERol.ROLE_TECNICO)
                                .orElseThrow(() -> new RuntimeException("Error: Rol de técnico no encontrado."));
                        roles.add(tecnicoRol);
                        break;
                    default:
                        // Si se proporciona un rol no reconocido, se asigna ROLE_CLIENTE por defecto
                        Rol defaultRol = rolRepository.findByNombre(ERol.ROLE_CLIENTE)
                                .orElseThrow(() -> new RuntimeException("Error: Rol de cliente no encontrado."));
                        roles.add(defaultRol);
                }
            });
        }

        user.setRoles(roles);
        usuarioRepository.save(user);

        List<String> assignedRoles = user.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toList());

        return new RegisterResponse("User registered successfully!",
                user.getNombre(),
                user.getUsername(),
                user.getEmail(),
                assignedRoles);

    }

}
