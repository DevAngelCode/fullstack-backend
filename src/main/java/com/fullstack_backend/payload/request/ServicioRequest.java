package com.fullstack_backend.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicioRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotNull
    private Double precio;
}
