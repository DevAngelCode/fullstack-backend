package com.fullstack_backend.service;

import com.fullstack_backend.model.Servicio;
import com.fullstack_backend.payload.request.ServicioRequest;
import com.fullstack_backend.payload.response.ServicioResponse;

import java.util.List;
import java.util.Optional;

public interface IServicioService {
    List<ServicioResponse> getAllServicios();
    Optional<ServicioResponse> getServicioById(Long id);
    ServicioResponse createServicio(ServicioRequest servicioRequest);
    ServicioResponse updateServicio(Long id, ServicioRequest servicioRequest);
    void deleteServicio(Long id);
}
