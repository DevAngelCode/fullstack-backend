package com.fullstack_backend.payload.response;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String message;
    private String imagenBase64;
    private String tipoImagen;

    public LoginResponse(String accessToken, Long id, String username, String email, List<String> roles, String message,
            String imagenBase64, String tipoImagen) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.message = message;
        this.imagenBase64 = imagenBase64;
        this.tipoImagen = tipoImagen;
    }

}
