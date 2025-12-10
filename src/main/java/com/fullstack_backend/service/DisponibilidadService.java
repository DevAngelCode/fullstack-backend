package com.fullstack_backend.service;

import com.fullstack_backend.payload.response.HorarioDisponibleResponse;

import java.time.LocalDate;
import java.util.List;

public interface DisponibilidadService {

    List<HorarioDisponibleResponse> getHorariosDisponibles(Long sedeId, LocalDate fecha);
}
