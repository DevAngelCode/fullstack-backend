package com.fullstack_backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicioResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
}
