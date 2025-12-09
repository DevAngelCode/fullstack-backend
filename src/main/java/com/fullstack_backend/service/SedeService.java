package com.fullstack_backend.service;

import com.fullstack_backend.payload.request.SedeRequest;
import com.fullstack_backend.payload.response.SedeResponse;

import java.util.List;

public interface SedeService {
    List<SedeResponse> getAllSedes();

    SedeResponse getSedeById(Long id);

    SedeResponse createSede(SedeRequest sedeRequest);

    SedeResponse updateSede(Long id, SedeRequest sedeRequest);

    void deleteSede(Long id);
}
