package com.fullstack_backend.service;

import com.fullstack_backend.payload.request.CitaRequest;
import com.fullstack_backend.payload.response.CitaResponse;

import java.util.List;

public interface CitaService {

    CitaResponse crearCita(CitaRequest citaRequest, String username);

    List<CitaResponse> getMisCitas(String username);

    CitaResponse getCitaById(Long id, String username);

    CitaResponse cancelarCita(Long id, String username);

    CitaResponse updateEstadoCita(Long id, String nuevoEstado, String username);

    List<CitaResponse> getAllCitas(); // Admin only

    byte[] generarReciboPdf(Long id, String username);
}
