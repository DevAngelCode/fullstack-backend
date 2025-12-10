package com.fullstack_backend.service.impl;

import com.fullstack_backend.enums.ERol;
import com.fullstack_backend.model.Rol;
import com.fullstack_backend.model.Sede;
import com.fullstack_backend.model.Servicio;
import com.fullstack_backend.model.Usuario;
import com.fullstack_backend.payload.request.UserManagementRequest;
import com.fullstack_backend.payload.response.UserManagementResponse;
import com.fullstack_backend.repository.RolRepository;
import com.fullstack_backend.repository.SedeRepository;
import com.fullstack_backend.repository.ServicioRepository;
import com.fullstack_backend.repository.UsuarioRepository;
import com.fullstack_backend.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserManagementResponse> getAllUsers() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserManagementResponse getUserById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToResponse(usuario);
    }

    @Override
    @Transactional
    public UserManagementResponse createUser(UserManagementRequest request) {
        // Validate password is provided for creation
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("La contraseña es requerida para crear un usuario");
        }

        // Validate password length
        if (request.getPassword().length() < 6 || request.getPassword().length() > 40) {
            throw new RuntimeException("La contraseña debe tener entre 6 y 40 caracteres");
        }

        // Validate username and email don't exist
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);

        // Assign roles
        Set<Rol> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                ERol eRol = ERol.valueOf(roleName);
                Rol rol = rolRepository.findByNombre(eRol)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
                roles.add(rol);
            }
        } else {
            // Default role: CLIENTE
            Rol clienteRol = rolRepository.findByNombre(ERol.ROLE_CLIENTE)
                    .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
            roles.add(clienteRol);
        }
        usuario.setRoles(roles);

        // If technician, assign sede and servicios
        if (request.getRoles() != null && request.getRoles().contains("ROLE_TECNICO")) {
            if (request.getSedeId() != null) {
                Sede sede = sedeRepository.findById(request.getSedeId())
                        .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
                usuario.setSede(sede);
            }

            if (request.getServicioIds() != null && !request.getServicioIds().isEmpty()) {
                Set<Servicio> servicios = new HashSet<>();
                for (Long servicioId : request.getServicioIds()) {
                    Servicio servicio = servicioRepository.findById(servicioId)
                            .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + servicioId));
                    servicios.add(servicio);
                }
                usuario.setServicios(servicios);
            }
        }

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return mapToResponse(savedUsuario);
    }

    @Override
    @Transactional
    public UserManagementResponse updateUser(Long id, UserManagementRequest request, String currentUsername) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Prevent self-modification
        if (usuario.getUsername().equals(currentUsername)) {
            throw new RuntimeException("No puedes modificar tu propia cuenta");
        }

        // Update basic fields
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);

        // Update password only if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            // Validate password length
            if (request.getPassword().length() < 6 || request.getPassword().length() > 40) {
                throw new RuntimeException("La contraseña debe tener entre 6 y 40 caracteres");
            }
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update roles
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Rol> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                ERol eRol = ERol.valueOf(roleName);
                Rol rol = rolRepository.findByNombre(eRol)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
                roles.add(rol);
            }
            usuario.setRoles(roles);
        }

        // Update sede and servicios if technician
        if (request.getRoles() != null && request.getRoles().contains("ROLE_TECNICO")) {
            if (request.getSedeId() != null) {
                Sede sede = sedeRepository.findById(request.getSedeId())
                        .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
                usuario.setSede(sede);
            } else {
                usuario.setSede(null);
            }

            if (request.getServicioIds() != null) {
                Set<Servicio> servicios = new HashSet<>();
                for (Long servicioId : request.getServicioIds()) {
                    Servicio servicio = servicioRepository.findById(servicioId)
                            .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + servicioId));
                    servicios.add(servicio);
                }
                usuario.setServicios(servicios);
            } else {
                usuario.getServicios().clear();
            }
        } else {
            // Not a technician, clear sede and servicios
            usuario.setSede(null);
            usuario.getServicios().clear();
        }

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return mapToResponse(updatedUsuario);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, String currentUsername) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Prevent self-deletion
        if (usuario.getUsername().equals(currentUsername)) {
            throw new RuntimeException("No puedes eliminar tu propia cuenta");
        }

        // Soft delete by disabling
        usuario.setEnabled(false);
        usuarioRepository.save(usuario);
    }

    private UserManagementResponse mapToResponse(Usuario usuario) {
        UserManagementResponse response = new UserManagementResponse();
        response.setId(usuario.getId());
        response.setUsername(usuario.getUsername());
        response.setEmail(usuario.getEmail());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setTelefono(usuario.getTelefono());
        response.setEnabled(usuario.getEnabled());

        // Map roles
        Set<String> roleNames = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toSet());
        response.setRoles(roleNames);

        // Map sede if exists
        if (usuario.getSede() != null) {
            response.setSedeId(usuario.getSede().getId());
            response.setSedeNombre(usuario.getSede().getNombre());
        }

        // Map servicios if exist
        if (usuario.getServicios() != null && !usuario.getServicios().isEmpty()) {
            Set<Long> servicioIds = usuario.getServicios().stream()
                    .map(Servicio::getId)
                    .collect(Collectors.toSet());
            response.setServicioIds(servicioIds);

            Set<String> servicioNombres = usuario.getServicios().stream()
                    .map(Servicio::getNombre)
                    .collect(Collectors.toSet());
            response.setServicioNombres(servicioNombres);
        }

        return response;
    }
}
