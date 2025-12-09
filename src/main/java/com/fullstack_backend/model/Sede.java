package com.fullstack_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "sedes")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imagenData;

    private String tipoImagen;
}
