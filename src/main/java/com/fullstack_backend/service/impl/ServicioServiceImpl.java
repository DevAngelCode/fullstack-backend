package com.fullstack_backend.service.impl;

import com.fullstack_backend.model.Servicio;
import com.fullstack_backend.payload.request.ServicioRequest;
import com.fullstack_backend.payload.response.ServicioResponse;
import com.fullstack_backend.repository.ServicioRepository;
import com.fullstack_backend.service.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioServiceImpl implements IServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Override
    public List<ServicioResponse> getAllServicios() {
        return servicioRepository.findAll().stream()
                .map(servicio -> {
                    ServicioResponse response = new ServicioResponse();
                    response.setId(servicio.getId());
                    response.setNombre(servicio.getNombre());
                    response.setDescripcion(servicio.getDescripcion());
                    response.setPrecio(servicio.getPrecio());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ServicioResponse> getServicioById(Long id) {
        return servicioRepository.findById(id)
                .map(servicio -> {
                    ServicioResponse response = new ServicioResponse();
                    response.setId(servicio.getId());
                    response.setNombre(servicio.getNombre());
                    response.setDescripcion(servicio.getDescripcion());
                    response.setPrecio(servicio.getPrecio());
                    return response;
                });
    }

    @Override
    public ServicioResponse createServicio(ServicioRequest servicioRequest) {
        Servicio servicio = new Servicio();
        servicio.setNombre(servicioRequest.getNombre());
        servicio.setDescripcion(servicioRequest.getDescripcion());
        servicio.setPrecio(servicioRequest.getPrecio());
        Servicio savedServicio = servicioRepository.save(servicio);

        ServicioResponse response = new ServicioResponse();
        response.setId(savedServicio.getId());
        response.setNombre(savedServicio.getNombre());
        response.setDescripcion(savedServicio.getDescripcion());
        response.setPrecio(savedServicio.getPrecio());
        return response;
    }

    @Override
    public ServicioResponse updateServicio(Long id, ServicioRequest servicioRequest) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: " + id));

        servicio.setNombre(servicioRequest.getNombre());
        servicio.setDescripcion(servicioRequest.getDescripcion());
        servicio.setPrecio(servicioRequest.getPrecio());

        Servicio updatedServicio = servicioRepository.save(servicio);

        ServicioResponse response = new ServicioResponse();
        response.setId(updatedServicio.getId());
        response.setNombre(updatedServicio.getNombre());
        response.setDescripcion(updatedServicio.getDescripcion());
        response.setPrecio(updatedServicio.getPrecio());
        return response;
    }

    @Override
    public void deleteServicio(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: " + id));
        servicioRepository.delete(servicio);
    }
}
