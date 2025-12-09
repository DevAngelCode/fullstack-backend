package com.fullstack_backend.service.impl;

import com.fullstack_backend.model.Sede;
import com.fullstack_backend.payload.request.SedeRequest;
import com.fullstack_backend.payload.response.SedeResponse;
import com.fullstack_backend.repository.SedeRepository;
import com.fullstack_backend.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SedeServiceImpl implements SedeService {

    @Autowired
    private SedeRepository sedeRepository;

    @Override
    public List<SedeResponse> getAllSedes() {
        return sedeRepository.findAll().stream()
                .map(sede -> {
                    SedeResponse response = new SedeResponse();
                    response.setId(sede.getId());
                    response.setNombre(sede.getNombre());
                    response.setDireccion(sede.getDireccion());
                    if (sede.getImagenData() != null) {
                        String mimeType = sede.getTipoImagen() != null ? sede.getTipoImagen() : "image/jpeg";
                        response.setTipoImagen(mimeType);
                        String base64Data = Base64.getEncoder().encodeToString(sede.getImagenData());
                        response.setImagenBase64("data:" + mimeType + ";base64," + base64Data);
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SedeResponse getSedeById(Long id) {
        return sedeRepository.findById(id)
                .map(sede -> {
                    SedeResponse response = new SedeResponse();
                    response.setId(sede.getId());
                    response.setNombre(sede.getNombre());
                    response.setDireccion(sede.getDireccion());
                    if (sede.getImagenData() != null) {
                        String mimeType = sede.getTipoImagen() != null ? sede.getTipoImagen() : "image/jpeg";
                        response.setTipoImagen(mimeType);
                        String base64Data = Base64.getEncoder().encodeToString(sede.getImagenData());
                        response.setImagenBase64("data:" + mimeType + ";base64," + base64Data);
                    }
                    return response;
                })
                .orElseThrow(() -> new RuntimeException("Sede no encontrada con id: " + id));
    }

    @Override
    public SedeResponse createSede(SedeRequest sedeRequest) {
        Sede sede = new Sede();
        sede.setNombre(sedeRequest.getNombre());
        sede.setDireccion(sedeRequest.getDireccion());

        if (sedeRequest.getImagenBase64() != null && !sedeRequest.getImagenBase64().isEmpty()) {
            try {
                String base64String = sedeRequest.getImagenBase64();

                if (sedeRequest.getTipoImagen() != null && !sedeRequest.getTipoImagen().isEmpty()) {
                    sede.setTipoImagen(sedeRequest.getTipoImagen());
                }

                if (base64String.contains(",")) {
                    String[] parts = base64String.split(",");
                    if (sede.getTipoImagen() == null) {
                        String mimeType = parts[0].substring(5, parts[0].indexOf(";"));
                        sede.setTipoImagen(mimeType);
                    }
                    sede.setImagenData(Base64.getDecoder().decode(parts[1]));
                } else {
                    if (sede.getTipoImagen() == null) {
                        sede.setTipoImagen("image/jpeg");
                    }
                    sede.setImagenData(Base64.getDecoder().decode(base64String));
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Base64 image string");
            }
        }

        Sede savedSede = sedeRepository.save(sede);

        SedeResponse response = new SedeResponse();
        response.setId(savedSede.getId());
        response.setNombre(savedSede.getNombre());
        response.setDireccion(savedSede.getDireccion());
        if (savedSede.getImagenData() != null) {
            String mimeType = savedSede.getTipoImagen() != null ? savedSede.getTipoImagen() : "image/jpeg";
            response.setTipoImagen(mimeType);
            String base64Data = Base64.getEncoder().encodeToString(savedSede.getImagenData());
            response.setImagenBase64("data:" + mimeType + ";base64," + base64Data);
        }
        return response;
    }

    @Override
    public SedeResponse updateSede(Long id, SedeRequest sedeRequest) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada con id: " + id));

        sede.setNombre(sedeRequest.getNombre());
        sede.setDireccion(sedeRequest.getDireccion());

        if (sedeRequest.getImagenBase64() != null && !sedeRequest.getImagenBase64().isEmpty()) {
            try {
                String base64String = sedeRequest.getImagenBase64();

                if (sedeRequest.getTipoImagen() != null && !sedeRequest.getTipoImagen().isEmpty()) {
                    sede.setTipoImagen(sedeRequest.getTipoImagen());
                }

                if (base64String.contains(",")) {
                    String[] parts = base64String.split(",");
                    if (sede.getTipoImagen() == null) {
                        String mimeType = parts[0].substring(5, parts[0].indexOf(";"));
                        sede.setTipoImagen(mimeType);
                    }
                    sede.setImagenData(Base64.getDecoder().decode(parts[1]));
                } else {
                    if (sede.getTipoImagen() == null) {
                        sede.setTipoImagen("image/jpeg");
                    }
                    sede.setImagenData(Base64.getDecoder().decode(base64String));
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Base64 image string");
            }
        }

        Sede updatedSede = sedeRepository.save(sede);

        SedeResponse response = new SedeResponse();
        response.setId(updatedSede.getId());
        response.setNombre(updatedSede.getNombre());
        response.setDireccion(updatedSede.getDireccion());
        if (updatedSede.getImagenData() != null) {
            String mimeType = updatedSede.getTipoImagen() != null ? updatedSede.getTipoImagen() : "image/jpeg";
            response.setTipoImagen(mimeType);
            String base64Data = Base64.getEncoder().encodeToString(updatedSede.getImagenData());
            response.setImagenBase64("data:" + mimeType + ";base64," + base64Data);
        }
        return response;
    }

    @Override
    public void deleteSede(Long id) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada con id: " + id));
        sedeRepository.delete(sede);
    }
}
