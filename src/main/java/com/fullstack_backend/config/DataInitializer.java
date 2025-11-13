package com.fullstack_backend.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fullstack_backend.enums.ERol;
import com.fullstack_backend.model.Rol;
import com.fullstack_backend.model.Usuario;
import com.fullstack_backend.repository.RolRepository;
import com.fullstack_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (rolRepository.count() == 0) {
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre(ERol.ROLE_ADMIN);
            rolRepository.save(rolAdmin);

            Rol rolCliente = new Rol();
            rolCliente.setNombre(ERol.ROLE_CLIENTE);
            rolRepository.save(rolCliente);

            Rol rolTecnico = new Rol();
            rolTecnico.setNombre(ERol.ROLE_TECNICO);
            rolRepository.save(rolTecnico);
        }

        if (usuarioRepository.count() == 0) {
            // Create a default admin user
            Rol adminRole = rolRepository.findByNombre(ERol.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role not found."));

            Usuario adminUser = new Usuario();
            adminUser.setNombre("Admin");
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("password")); // Encoded password
            Set<Rol> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminUser.setRoles(adminRoles);
            usuarioRepository.save(adminUser);

            System.out.println("Default admin user created: username='admin', password='password'");

            // Create a default technician user
            Rol tecnicoRole = rolRepository.findByNombre(ERol.ROLE_TECNICO)
                    .orElseThrow(() -> new RuntimeException("Error: Tecnico Role not found."));

            Usuario tecnicoUser = new Usuario();
            tecnicoUser.setNombre("Tecnico");
            tecnicoUser.setUsername("tecnico");
            tecnicoUser.setEmail("tecnico@example.com");
            tecnicoUser.setPassword(passwordEncoder.encode("password")); // Encoded password
            Set<Rol> tecnicoRoles = new HashSet<>();
            tecnicoRoles.add(tecnicoRole);
            tecnicoUser.setRoles(tecnicoRoles);
            usuarioRepository.save(tecnicoUser);

            System.out.println("Default technician user created: username='tecnico', password='password'");
        }
    }

}
