package com.fullstack_backend.service.impl;

import com.fullstack_backend.model.Servicio;
import com.fullstack_backend.payload.request.ServicioRequest;
import com.fullstack_backend.payload.response.ServicioResponse;
import com.fullstack_backend.repository.ServicioRepository;
import com.fullstack_backend.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioServiceImpl implements ServicioService {

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
                    if (servicio.getImagenData() != null) {
                        String mimeType = servicio.getTipoImagen() != null ? servicio.getTipoImagen() : "image/jpeg";
                        response.setTipoImagen(mimeType);
                        String base64Data = Base64.getEncoder().encodeToString(servicio.getImagenData());
                        response.setImagenBase64("data:" + mimeType + ";base64," + base64Data);
                    }
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
                    if (servicio.getImagenData() != null) {
                        String mimeType = servicio.getTipoImagen() != null ? servicio.getTipoImagen() : "image/jpeg";
                        response.setTipoImagen(mimeType);
                        String base64Data = Base64.getEncoder().encodeToString(servicio.getImagenData());
                        response.setImagenBase64("data:" + mimeType + ";base64," + base64Data);
                    }
                    return response;
                });
    }

    @Override
    public ServicioResponse createServicio(ServicioRequest servicioRequest) {
        Servicio servicio = new Servicio();
        servicio.setNombre(servicioRequest.getNombre());
        servicio.setDescripcion(servicioRequest.getDescripcion());
        servicio.setPrecio(servicioRequest.getPrecio());

        if (servicioRequest.getImagenBase64() != null && !servicioRequest.getImagenBase64().isEmpty()) {
            try {
                String base64String = servicioRequest.getImagenBase64();

                // Manual processing: Check if type is explicitly provided
                if (servicioRequest.getTipoImagen() != null && !servicioRequest.getTipoImagen().isEmpty()) {
                    servicio.setTipoImagen(servicioRequest.getTipoImagen());
                }

                if (base64String.contains(",")) {
                    String[] parts = base64String.split(",");
                    // Extract if not manually set
                    if (servicio.getTipoImagen() == null) {
                        String mimeType = parts[0].substring(5, parts[0].indexOf(";"));
                        servicio.setTipoImagen(mimeType);
                    }
                    servicio.setImagenData(Base64.getDecoder().decode(parts[1]));
                } else {
                    // Fallback manual default
                    if (servicio.getTipoImagen() == null) {
                        servicio.setTipoImagen("image/jpeg");
                    }
                    servicio.setImagenData(Base64.getDecoder().decode(base64String));
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Base64 image string");
            }
        }

        Servicio savedServicio = servicioRepository.save(servicio);

        ServicioResponse response = new ServicioResponse();
        response.setId(savedServicio.getId());
        response.setNombre(savedServicio.getNombre());
        response.setDescripcion(savedServicio.getDescripcion());
        response.setPrecio(savedServicio.getPrecio());
        if (savedServicio.getImagenData() != null) {
            String mimeType = savedServicio.getTipoImagen() != null ? savedServicio.getTipoImagen() : "image/jpeg";
            response.setTipoImagen(mimeType);
            String base64Data = Base64.getEncoder().encodeToString(savedServicio.getImagenData());
            response.setImagenBase64("data:" + mimeType + ";base64," + base64Data);
        }
        return response;
    }

    @Override
    public ServicioResponse updateServicio(Long id, ServicioRequest servicioRequest) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: " + id));

        servicio.setNombre(servicioRequest.getNombre());
        servicio.setDescripcion(servicioRequest.getDescripcion());
        servicio.setPrecio(servicioRequest.getPrecio());

        // Only update image if a new one is provided (non-empty string)
        if (servicioRequest.getImagenBase64() != null && !servicioRequest.getImagenBase64().isEmpty()) {
            try {
                String base64String = servicioRequest.getImagenBase64();

                // Manual processing: Check if type is explicitly provided
                if (servicioRequest.getTipoImagen() != null && !servicioRequest.getTipoImagen().isEmpty()) {
                    servicio.setTipoImagen(servicioRequest.getTipoImagen());
                }

                if (base64String.contains(",")) {
                    String[] parts = base64String.split(",");
                    // Extract if not manually set
                    if (servicio.getTipoImagen() == null) {
                        String mimeType = parts[0].substring(5, parts[0].indexOf(";"));
                        servicio.setTipoImagen(mimeType);
                    }
                    servicio.setImagenData(Base64.getDecoder().decode(parts[1]));
                } else {
                    // Fallback manual default
                    if (servicio.getTipoImagen() == null) {
                        servicio.setTipoImagen("image/jpeg");
                    }
                    servicio.setImagenData(Base64.getDecoder().decode(base64String));
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Base64 image string");
            }
        }

        Servicio updatedServicio = servicioRepository.save(servicio);

        ServicioResponse response = new ServicioResponse();
        response.setId(updatedServicio.getId());
        response.setNombre(updatedServicio.getNombre());
        response.setDescripcion(updatedServicio.getDescripcion());
        response.setPrecio(updatedServicio.getPrecio());
        if (updatedServicio.getImagenData() != null) {
            String mimeType = updatedServicio.getTipoImagen() != null ? updatedServicio.getTipoImagen() : "image/jpeg";
            response.setTipoImagen(mimeType);
            String base64Data = Base64.getEncoder().encodeToString(updatedServicio.getImagenData());
            response.setImagenBase64("data:" + mimeType + ";base64," + base64Data);
        }
        return response;
    }

    @Override
    public void deleteServicio(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: " + id));
        servicioRepository.delete(servicio);
    }
}
