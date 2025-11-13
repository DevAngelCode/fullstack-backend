package com.fullstack_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{

    @Override
    public void addCorsMappings(CorsRegistry registry) {
         // Configura CORS para todos los endpoints
        registry.addMapping("/**") // Aplica a todos los endpoints
                .allowedOrigins("http://localhost:4200") // Orígenes permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With") // Encabezados permitidos
                .allowCredentials(true) // Permite el envío de credenciales (cookies, cabeceras de autenticación, etc.)
                .maxAge(3600); // Tiempo máximo de cacheo de la configuración CORS en el navegador (en segundos)
    }
}