package com.fullstack_backend.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.fullstack_backend.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    // Obtiene la clave para la firma del JWT
    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

     /**
     * Genera un token JWT incluyendo el nombre de usuario y los roles.
     * @param authentication La autenticación del usuario
     * @return El token JWT generado
     */
    public String generateJwtToken(Authentication authentication) {
        Usuario userPrincipal = (Usuario) authentication.getPrincipal();

        // Obtener los roles como lista de strings (ej: ["ROLE_ADMIN", "ROLE_USER"])
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Generación del token con la firma HMACSHA y roles como claims
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())  // Setea el nombre de usuario como sujeto
                .claim("roles", roles)  // Asegúrate de que los roles sean un array o lista de strings
                .setIssuedAt(new Date())  // Fecha de emisión
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration)) // Expiración del token
                .signWith(key(), SignatureAlgorithm.HS256)  // Firma con la clave secreta
                .compact();  // Retorna el token compactado
    }

    /**
     * Obtiene el nombre de usuario del token JWT.
     * @param token El token JWT
     * @return El nombre de usuario extraído del token
     * @throws RuntimeException Si el token ha expirado o es inválido
     */
    public String getUserNameFromJwtToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key())  // Usar la clave para verificar la firma
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();  // Extrae el nombre de usuario
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("El token ha expirado", e);  // Manejo de expiración
        } catch (JwtException e) {
            throw new RuntimeException("Token inválido", e);  // Manejo de token inválido
        }
    }

    /**
     * Valida el token JWT.
     * @param authToken El token JWT
     * @return true si el token es válido, false si no lo es
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())  // Usar la clave para verificar la firma
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();  // Extrae las claims del token

            // Verifica si el token ha expirado
            return !isTokenExpired(claims); 
        } catch (JwtException e) {
            return false;  // Si ocurre cualquier excepción de JWT, el token es inválido
        }
    }

    /**
     * Verifica si el token ha expirado.
     * @param claims Las claims del token
     * @return true si el token ha expirado, false si no lo ha hecho
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }


}
